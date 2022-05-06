import com.fasterxml.jackson.annotation.JsonProperty;

public class CacheValue {

    @JsonProperty
    int id;
    @JsonProperty
    int hash;
    @JsonProperty
    String res;

    String toJsonString(){
        return new StringBuilder().append("{ \"id\":").append(id).append(",\"hash\":").
                append(hash).append(",\"res\":\"").append(res).append("\"}").toString();
    }
    String toCsvString() {
        return new StringBuilder().append(id).append(",").append(hash).append(",").append(res).toString();
    }

    public static CacheValue toCacheValue(String cacheValue) {
        String[] splits = cacheValue.split(",");
        CacheValue value = new CacheValue();
        value.id = Integer.parseInt(splits[0]);
        value.hash = Integer.parseInt(splits[1]);
        value.res = splits[2];
        return value;
    }

    @Override
    public boolean equals(Object o) {
        CacheValue other = (CacheValue)o;
        return other.id == this.id && other.hash == this.hash && other.res.equals(this.res);
    }


}

