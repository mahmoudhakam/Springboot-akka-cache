package com.se.onprem.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.se.onprem.ParametricBaseTest;
import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.FeatureDTO;
import com.se.onprem.strategy.AddPartsStrategy;
import com.se.onprem.util.JsonHandler;

@Test
public class ParametricControllerTest extends ParametricBaseTest
{

@Autowired
AddPartsStrategy addPartsStrategy;

    @Test(enabled = false)
    void shouldNotReturnNullWhenRequestingWithSE2Startegy() {
        // ParametricController controller = new ParametricController(new SE2PlFeatureStrategyImpl());
        // String fetaures = controller.getFeaturesAndValues();
        // Assert.assertNotEquals(fetaures, null);
    }
    @Test(enabled = true)
    void shouldCreateStringFromObjectSuccessfully() {
    	CustomerPart p1=new CustomerPart();
    	CustomerPart p2=new CustomerPart();
    	FeatureDTO f1=new FeatureDTO();
    	FeatureDTO f2=new FeatureDTO();
    	List<CustomerPart>inputParts=new ArrayList<>(2);
    	List<FeatureDTO>features=new ArrayList<>(2);
    	List<FeatureDTO>features2=new ArrayList<>(2);
//    	p1.setComID("76780619");
    	p1.setCpn("dx1");
    	p1.setMan("Thin Film Technology");
    	p1.setMpn("RR2632N2150D-T1-LF");
//    	p2.setComID("79327978");
    	p2.setCpn("cpn2");
    	p2.setMan("United Chemi Con");
    	p2.setMpn("FTK200VB120M15AA");
    	f1.setFeatureName("cnRohs");
    	f1.setFeauterValue("affected");
    	f2.setFeatureName("lc");
    	f2.setFeauterValue("active");
    	features.add(f1);
    	features.add(f2);
    	features2.add(f1);
    	inputParts.add(p1);
    	inputParts.add(p2);
    	p1.setCustomFeatures(features);
    	p2.setCustomFeatures(features2);
    	JsonHandler<CustomerPart>parts=new JsonHandler<>();
    	String out=parts.convertListToJon(inputParts);    	
    	System.out.println(out);
//    	addPartsStrategy.addParts(inputParts);
    	Assert.assertNotEquals(0, inputParts.get(0).getPartID(), "insertion failed");
    	
    }
    
}
