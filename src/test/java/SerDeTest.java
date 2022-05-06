import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Note that the below line is required to run the JSON method first. Actually for the first test method
// Test result execution time is more than the actual method run time.
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SerDeTest {

    private char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ':', ';', ',', '#', '@', '$', '%', '^', '&', '*', '!'};

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testJsonLoadTime() throws Exception {
        long startTime = System.currentTimeMillis();
        List<CacheValue> cacheValues = generateLoad(100000);
        long loadTime = System.currentTimeMillis()- startTime;
        startTime = System.currentTimeMillis();
        List<String> jsonStrings = new ArrayList<>();
        for (CacheValue cacheValue : cacheValues) {
            jsonStrings.add(cacheValue.toJsonString());
        }
        long endSerializationTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        List<CacheValue> newCacheValues = new ArrayList<>();
        for (String json : jsonStrings) {
            newCacheValues.add(objectMapper.readValue(json.getBytes(), CacheValue.class));
        }
        long endDeserializationTime = System.currentTimeMillis() - startTime;
        Assert.assertEquals(100000, newCacheValues.size());
        System.out.println(String.format("JSON Ser Time %s and DeSer Time %s and loadTime %s",
                endSerializationTime, endDeserializationTime, loadTime));
        Assert.assertTrue(matchEntries(cacheValues, newCacheValues));
    }

    @Test
    public void testStringCsvLoadTime() {
        long startTime = System.currentTimeMillis();
        List<CacheValue> cacheValues = generateLoad(100000);
        long loadTime = System.currentTimeMillis()- startTime;
        startTime = System.currentTimeMillis();
        List<String> csvStrings = new ArrayList<>();
        for (CacheValue cacheValue : cacheValues) {
            csvStrings.add(cacheValue.toCsvString());
        }
        long endSerializationTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        List<CacheValue> newCacheValues = new ArrayList<>();
        for (String csv : csvStrings) {
            newCacheValues.add(CacheValue.toCacheValue(csv));
        }
        Assert.assertEquals(100000, newCacheValues.size());
        long endDeserializationTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("CSV Ser Time %s and DeSer Time %s and loadTime %s",
                endSerializationTime, endDeserializationTime, loadTime));
        Assert.assertTrue(matchEntries(cacheValues, newCacheValues));
    }

    private boolean matchEntries(List<CacheValue> old, List<CacheValue> newValues) {
        HashMap<Integer, List<CacheValue>> newValueMap = new HashMap<>();
        for (CacheValue cacheValue : newValues) {
            List<CacheValue> values = newValueMap.getOrDefault(cacheValue.id, new ArrayList<>());
            values.add(cacheValue);
            newValueMap.put(cacheValue.id, values);
        }

        for (CacheValue cacheValue : old) {
            List<CacheValue> cacheValues = newValueMap.get(cacheValue.id);
            boolean found = false;
            for (int i = 0; i < cacheValues.size(); i++) {
                if (cacheValue.equals(cacheValues.get(i))) {
                    cacheValues.remove(cacheValues.get(i));
                    found = true;
                }
            }
            if (found == false){
                return false;
            }
        }
        return true;
    }

    private List<CacheValue> generateLoad(int loadCount) {
        List<CacheValue> cacheValues = new ArrayList<>();
        for (int i = 0; i < loadCount; i++) {
            cacheValues.add(new CacheValue());
        }
        populateId(cacheValues, loadCount, 1000, 1000000);
        populateHash(cacheValues, loadCount, 1000000, 10000000);
        populateResource(cacheValues, loadCount, 50, 100);
        return cacheValues;
    }

    private void populateId(List<CacheValue> cacheValues, int loadCount, int lowRange, int highRange) {
        for (int i = 0; i < loadCount; i++) {
            cacheValues.get(i).id = (int) (lowRange + Math.random() * highRange);
        }
    }

    private void populateHash(List<CacheValue> cacheValues, int loadCount, int lowRange, int highRange) {
        for (int i = 0; i < loadCount; i++) {
            cacheValues.get(i).hash = (int) (lowRange + (highRange - lowRange) * Math.random());
        }
    }

    private void populateResource(List<CacheValue> cacheValues, int loadCount, int lowRange, int highRange) {
        for (int i = 0; i < loadCount; i++) {
            cacheValues.get(i).res = getResourceString(lowRange, highRange);
        }
    }

    private String getResourceString(int lowRange, int highRange) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = lowRange + (int) ((highRange - lowRange) * Math.random());
        for (int i = 0; i < length; i++) {
            stringBuilder.append(chars[(int) Math.random() * chars.length]);
        }
        return stringBuilder.toString();
    }
}
