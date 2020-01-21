package com.se.onprem;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.se.onprem.CustomerAPI;
import com.se.onprem.configuration.APIConfiguration;

/**
 * @author Mahmoud_Abdelhakam
 */

@Test
@ContextConfiguration(classes = APIConfiguration.class)
@SpringBootTest(classes = CustomerAPI.class)
public class ParametricBaseTest extends AbstractTestNGSpringContextTests {

    /**
     * Every test class should extends this class
     * 
     **/

}
