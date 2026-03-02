package org.example.orderservice;

import org.example.orderservice.facade.OrderFacade;
import org.example.orderservice.mock.MockOrderDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderServiceApplication.class, args);
  }


  @Bean
  public CommandLineRunner clr(ApplicationContext ctx) {
    return args -> {
      OrderFacade facade = ctx.getBean(OrderFacade.class);

      MockOrderDataService mockOrderDataService = ctx.getBean(MockOrderDataService.class);

      while(true) {
        facade.placeOrder(mockOrderDataService.generateOrderRequest());

        Thread.sleep(2000);
      }
    };
  }

}
