package com.dissidia986.test.rongbao;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={/*"file:src/main/webapp/WEB-INF/wxiot-servlet.xml",*/ "classpath:spring/applicationContext.xml"})
 
public class TestBase {
    protected Log logger = LogFactory.getLog(TestBase.class);
   
    @Before
    //一些公用的“初始化”代码
    public void before(){
    }
}