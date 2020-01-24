package cafe.deadbeef.aprs_bridge;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AppConfig implements AsyncConfigurer {


	  @Override
	  public Executor getAsyncExecutor() {
	      return new SimpleAsyncTaskExecutor();
	  }

	  @Override
	  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
	      return new CustomAsyncExceptionHandler();
	  }

	  
  
}