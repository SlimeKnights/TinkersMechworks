package slimeknights.tmechworks.library;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JsonConfig {

    public static List<JsonBlacklist> blockBlacklist;

    public static void createJsonDefault(File configfile){
        if(!configfile.exists()) {
            List<JsonBlacklist> blockMap = new ArrayList<>();
            List emptyList = new ArrayList();

            blockMap.add(new JsonBlacklist(Blocks.SAND, Arrays.asList(EnumFacing.DOWN)));
            blockMap.add(new JsonBlacklist(Blocks.GRAVEL, Arrays.asList(EnumFacing.DOWN)));
            blockMap.add(new JsonBlacklist(Blocks.LEAVES, emptyList));


            Gson n = new Gson();
            String jsonOut = n.toJson(blockMap);
            //System.out.print(test);

            try {
                FileWriter writer = new FileWriter(configfile);
                writer.write(jsonOut);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readJson(File filePath){
        Type REVIEW_TYPE = new TypeToken<List<JsonBlacklist>>() {}.getType();
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(filePath));
            List<JsonBlacklist> data = gson.fromJson(reader, REVIEW_TYPE);
            blockBlacklist = data;
        }catch (FileNotFoundException e){}
    }

    public static void validateBlacklist(){
        for(JsonBlacklist list : blockBlacklist){
            if(!list.exists()){
                blockBlacklist.remove(list);
            }
        }
    }

}
