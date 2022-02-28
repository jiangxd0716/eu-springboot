# SpingBoot-Frame
**springboot 通用框架**


````
security:
   authority: false # 是否开启接口授权
   sign: true  # 是否开启接口签名校验
   tstimeout: 120000  # 时间戳有效期2分钟 (ms)
   signtimeout: 120000 # 签名有效期2分钟 (ms)
````
springboot整合jwt、拦截器、自定义注解实现接口权限控制

增加接口安全认证：时间戳、随机数、签名校验

可自定义开启


1、接口权限

````
/**
 * 接口权限注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authority {

    /**
     * 标识
     * 多个逗号分割
     *
     * @return
     */
    String mark() default "";

    /**
     * 描述信息
     *
     * @return
     */
    String name() default "";

}
````
需要添加权限的接口添加Authority注解即可，mark为权限标识。
若该接口允许多个权限标识授权访问，则多个权限标识用逗号拼接。

接口授权有以下几种方案：

一、若配置文件没有开启接口授权，则只需要校验token是否合法即可访问接口。

二、若配置文件开启接口授权，在用户登录的时候会把用户有权限的权限标识放入jwt中，当用户携带jwt访问接口时会先校验token是否合法。
然后获取当前方法的注解，判断是否有Authority注解，如果没有则实例化当前用户允许访问接口，如果有则从token中获取权限标识以及当前request的权限标识，
判断用户是否有该接口的权限，如果有则实例化当前用户允许访问接口，如果没有则返回401未授权。


2、接口安全校验

如果开启接口安全认证，接口需要携带时间戳ts、动态盐salt、签名sign。

当请求为get或者delete时，系统默认从params中获取参数，

当请求为post、put或者patch时，系统默认从body中获取参数。

如需调整，请修改如下代码：

````
/**
     * 从body中获取请求参数
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public Map<String, Object> getParam(HttpServletRequest request) throws IOException {
        Map<String, Object> paramMap = new HashMap<>();

        // get | delete 请求从url中获取参数
        if (HttpMethod.GET.name().equals(request.getMethod()) || HttpMethod.DELETE.name().equals(request.getMethod())) {
            // 请求参数
            String queryString = request.getQueryString();
            String[] split = queryString.split(StringPool.AMPERSAND);

            //参数转为map结构
            for (String s : split) {
                String[] paramSpilt = s.split(StringPool.EQUALS);
                paramMap.put(paramSpilt[0], paramSpilt.length == 2 ? paramSpilt[1] : "");
            }
        }

        // post | put | patch 请求从body中获取参数
        if (HttpMethod.POST.name().equals(request.getMethod()) || HttpMethod.PUT.name().equals(request.getMethod()) || HttpMethod.PATCH.name().equals(request.getMethod())) {
            // body参数转为key-value
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            paramMap = GsonUtil.getObject(responseStrBuilder.toString(), HashMap.class);
        }

        return paramMap;

    }
````

一、客户端时间戳校验：从请求参数中解析出ts客户端时间戳参数，然后获取服务端时间戳，判断两个时间戳是否在配置文件自定义的有效期内，
如果在则请求合法，如果不在则表示请求非法。

二、签名校验：客户端将接口参数、时间戳ts，按照顺序重排序，然后进行md5或者rsa、aes加密，然后转为大写的十六进制，便生成签名sign。
请求接口时除了接口所需参数外需要增加时间戳ts以及生成的签名sign。
服务端获取参数之后，重新计算签名sign，比对客户端传过来的签名以及服务端重新计算的签名，判断接口数据是否被修改过。
同时，将sign签名放入redis中并设置有效期，可以防止接口重复提交。

三、盐签名：也就是在二的基础上加一个随机参数salt盐，在进行加密的时候加入盐确保加密的随机性。

````
 /**
     * 接口参数签名校验
     *
     * @param request
     */
    public void vaildSign(HttpServletRequest request) {

        try {
            // 参数转为key-value
            Map<String, Object> paramMap = this.getParam(request);

            // 获取客户端时间戳、并校验
            String clientTs = String.valueOf(paramMap.get("ts"));
            if (StrUtil.isBlank(clientTs)) {
                throw new GlobalException(GlobalExceptionCode.ILLEGAL_REQUEST, "请传入时间戳ts");
            }

            // 获取取客户端签名
            String clientSign = String.valueOf(paramMap.get("sign"));
            if (StrUtil.isBlank(clientSign)) {
                throw new GlobalException(GlobalExceptionCode.ILLEGAL_REQUEST, "请传入签名sign");
            }

            this.vaildTimetamp(Long.valueOf(clientTs));
            log.info("时间戳校验通过");

            // 签名校验
            log.info("客户端签名==》【{}】", clientSign);

            // 服务端签名==》参数排序->md5盐加密->转为16进制大写
            String serverSign = SignUtil.signTopRequest(paramMap, paramMap.get("salt") != null ? String.valueOf(paramMap.get("salt")) : "");
            log.info("服务端签名==》【{}】", serverSign);
            if (!serverSign.equals(clientSign)) {
                throw new GlobalException(GlobalExceptionCode.SIGN_FAILED);
            } else {

                // setCache 存储签名
                RSetCache<Object> signSet = this.redissonClient.getSetCache(RedisKey.KEY_API_SIGN);
                // 添加监听事件==》过期则删除
                signSet.addListener((ExpiredObjectListener) name -> signSet.remove(serverSign));

                // 从redis中获取签名,若存在，则说明重复请求
                if (signSet.contains(serverSign)) {
                    throw new GlobalException(GlobalExceptionCode.REPEAT_REQUEST);
                } else {
                    // 不存在，则把签名缓存到redis，且设置过期时间
                    signSet.add(serverSign, signtimeout, TimeUnit.MILLISECONDS);
                }
            }

        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            throw new GlobalException(GlobalExceptionCode.SIGN_FAILED);
        }

    }
````

3、关于获取当前用户

当接口请求通过后，会实例化一个CurrentUser对象，里面的参数为ThreadLocal类型，线程私有的。
````
CurrentUser.init(Long.parseLong(jwt.getUserId()), jwt.getUsername());
````

在方法中直接获取当前用户id和用户名称
```
CurrentUser.getId(); // 获取当前用户id
CurrentUser.getUsername(); // 获取当前用户名称
```


**数据库保存操作日志**