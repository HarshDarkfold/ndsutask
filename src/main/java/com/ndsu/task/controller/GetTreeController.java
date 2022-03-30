package com.ndsu.task.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndsu.task.model.UserDetail;
import com.ndsu.task.service.GetJsonTreeService;

@RestController
public class GetTreeController {

	private GetJsonTreeService restService;

	public GetTreeController(GetJsonTreeService restService) {
		this.restService = restService;
	}

	@RequestMapping(value = "/usernames")
	@ResponseBody
	public String getTree() throws JsonMappingException, JsonProcessingException, IOException
	{
		String jsonTree = restService.getTreeJsonFeed("https://wings.it.ndsu.edu/iam/tree.json");
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String,String> guids = new HashMap<String,String>();
		String temp = "";
		
		guids = getGuidsListNames(jsonTree, mapper);
		
		Iterator<Entry<String, String>> itr
        = guids.entrySet().iterator();
		
	    while (itr.hasNext()) 
	    {
	    	Map.Entry<String, String> guid = (Map.Entry<String, String>) itr.next();
	    	
			String userJson = restService.getTreeJsonFeed("https://wings.it.ndsu.edu/iam/"+guid.getKey()+".json");
			
			UserDetail[] ud = mapper.readValue(userJson, UserDetail[].class);
			List<UserDetail> userDetails = new ArrayList<UserDetail>(Arrays.asList(ud));
				
			for (UserDetail userDetail : userDetails){
				temp = temp + userDetail.getUsername() + System.lineSeparator();
			}
			Files.write(Paths.get(guid.getValue()+".txt"), temp.getBytes());
			temp = "";
		}

		String output = "Process Completed";
		return output;
	}

	public HashMap<String,String> getGuidsListNames(String json, ObjectMapper mapper)
			throws JsonMappingException, JsonProcessingException, IOException {

		HashMap<String,String> guids = new HashMap<String,String>();
		Map<String, Object> nodes = null;
		nodes = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

		getGuidsAndListNames(nodes, guids);

		return guids;
	}

	private void getGuidsAndListNames(Map<String, Object> nodes, HashMap<String, String> hm) 
	{
		for(Entry<String, Object> entry : nodes.entrySet()) {
			if (entry.getValue() instanceof Map) 
			{
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) entry.getValue();
				getGuidsAndListNames(map,hm);
			} 
			else if (entry.getValue() instanceof List) 
			{
				List<?> list = (List<?>) entry.getValue();
				for(Object listEntry : list) {
					if (listEntry instanceof Map) 
					{
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) listEntry;
						if (map.containsKey("listName")) {
							hm.put(map.get("guid").toString(), map.get("listName").toString());
						}
						getGuidsAndListNames(map,hm);
					}
				}
			}
		}
	}
}
