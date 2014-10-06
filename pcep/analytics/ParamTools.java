package pcep.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import pcep.analytics.model.ModelParam;
import pcep.analytics.model.GridParam;

public class ParamTools {
	private static Logger log = Logger.getLogger(ParamTools.class);
	
	public static List<Map<String, Double>> getCombinations(ModelParam[] params) {
		for (ModelParam p : params) {
			p.reset();
		}
		
		List<Map<String, Double>> result = getCombinations(params, 0); 
		log.info("Found " + result.size() + " parameter combinations");
		return result;
	}
	
	public static Map<String, Double> toSingleMap(ModelParam[] params) {
		Map<String, Double> result = new HashMap<String, Double>();
		for (ModelParam param : params) {
			result.put(param.getName(), param.next());
		}
		return result;
	}
	
	private static List<Map<String, Double>> getCombinations(ModelParam[] params, int index) {
		List<Map<String, Double>> result = new ArrayList<Map<String, Double>>();
		if (params.length == 0) {
			return result;
		}
		
		ModelParam param = params[index];	
		Map<String, Double> c;
		Double d;
		
		if (index == params.length-1) {
			while(param.hasNext()) {
				c = new HashMap<String, Double>();
				c.put(param.getName(), param.next());
				result.add(c);
			}
			return result;
		}
		
		for (Map<String, Double> t : getCombinations(params, index+1)) {
			while (param.hasNext()) {
				d = param.next();
				c = new HashMap<String, Double>();
				c.putAll(t);
				c.put(param.getName(), d);
				result.add(c);
			}
			param.reset();
			index++;
		}
		return result;
	}
	

}
