import com.eu.frame.FrameApplication;
import com.eu.frame.common.rabbitmq.RabbitConfig;
import com.eu.frame.system.pojo.po.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = FrameApplication.class)
@RunWith(SpringRunner.class)
public class RabbitMqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void testSend() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
//        for (int i = 0; i < 10; i++) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, user);
//        }
    }
}
