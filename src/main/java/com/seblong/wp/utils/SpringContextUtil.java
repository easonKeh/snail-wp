package com.seblong.wp.utils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContextUtil implements ApplicationContextAware {
        private static ApplicationContext applicationContext; // Spring应用上下文环境

        /**
         * 获取对象
         *
         * @param name
         * @return Object 一个以所给名字注册的bean的实例
         * @throws org.springframework.beans.BeansException
         */
        public static<T>  T getBean(String name, Class<T> beanClass) throws BeansException {
                return applicationContext.getBean(name, beanClass);
        }

        public static<T>  T getBean( Class<T> beanClass) throws BeansException {
                return applicationContext.getBean(beanClass);
        }

        public static void registerBean(String beanName, Object singletonObject) {
                ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
                ConfigurableListableBeanFactory beanFactory = configurableApplicationContext.getBeanFactory();
                beanFactory.registerSingleton(beanName, singletonObject);
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
                SpringContextUtil.applicationContext = applicationContext;
        }
}