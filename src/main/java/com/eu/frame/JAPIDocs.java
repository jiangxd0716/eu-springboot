package com.eu.frame;

import io.github.yedaxia.apidocs.Docs;
import io.github.yedaxia.apidocs.DocsConfig;

/**
 * 接口文档生成器
 * 直接运行该文档, 即可在工程根目录下的 docs 文件夹中生成本项目的接口文档
 */
public class JAPIDocs {

    public static void main(String[] args) {

        String projectParentPath = System.getProperty("user.dir");
        String projectName = "test";


        DocsConfig config = new DocsConfig();
        config.setProjectPath(projectParentPath); // 项目根目录
        config.setProjectName("测试"); // 项目名称
        config.setApiVersion("V1.0");       // 声明该API的版本
        config.setDocsPath(projectParentPath + "\\docs\\" + projectName); // 生成API 文档所在目录
        config.setAutoGenerate(Boolean.TRUE);  // 配置自动生成
        Docs.buildHtmlDocs(config); // 执行生成文档

    }

}
