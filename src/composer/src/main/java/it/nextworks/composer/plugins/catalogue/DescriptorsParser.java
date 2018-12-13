package it.nextworks.composer.plugins.catalogue;

import java.io.BufferedReader;

/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;

@Service
public class DescriptorsParser {
	
	private static final Logger log = LoggerFactory.getLogger(DescriptorsParser.class);

	public static DescriptorTemplate fileToDescriptorTemplate(String fileName)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		DescriptorTemplate descriptorTemplate = mapper.readValue(new File(fileName), DescriptorTemplate.class);

		return descriptorTemplate;
	}
	
	public static DescriptorTemplate fileToDescriptorTemplate(File file)
			throws JsonParseException, JsonMappingException, IOException {
		log.debug("Trying to convert file to DescriptorTemplate");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		DescriptorTemplate descriptorTemplate = mapper.readValue(file, DescriptorTemplate.class);
		log.debug("Converted file to DescriptorTemplate");
		
		return descriptorTemplate;
	}

	public static DescriptorTemplate stringToDescriptorTemplate(String descriptor)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		//mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		DescriptorTemplate descriptorTemplate = mapper.readValue(descriptor, DescriptorTemplate.class);

		return descriptorTemplate;
	}
	
	public static String descriptorTemplateToString(DescriptorTemplate descriptor) 
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		//mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		String dt = mapper.writeValueAsString(descriptor);
		return dt;
	}
	
	public static File descriptorTempateToFile(DescriptorTemplate descriptor) throws JsonProcessingException {
		File file = null;
		String textDescriptor = DescriptorsParser.descriptorTemplateToString(descriptor);
		try {
			file = new File("/tmp/tmp-descriptor.yaml");
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(textDescriptor);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			log.error("Impossible to write descriptor to file. " + e.getMessage());
			return null;
		}
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("/tmp/tmp-descriptor.yaml"));
			for (String line; (line = br.readLine()) != null;) {
			    System.out.println(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return file;

	}
}

