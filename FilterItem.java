

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 具体查询条件
 **/
 @JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class FilterItem    
{
    
    @Valid
    @JsonProperty("operator")
    private String operator = null;
    
    @Valid
    @JsonProperty("value")
    private String value = null;
    
    @Valid
    @JsonProperty("range")
    private List<String> range = new ArrayList<String>();
    
    @Valid
    @JsonProperty("matchList")
    private List<String> matchList = new ArrayList<String>();
    
    @Valid
    @JsonProperty("and")
    private Map<String, List<FilterItem>> and = new HashMap<String, List<FilterItem>>();
    
    @Valid
    @JsonProperty("or")
    private Map<String, List<FilterItem>> or = new HashMap<String, List<FilterItem>>();
    
    
    /**
     * 匹配类型
    **/
    @JsonProperty("operator")
    public String getOperator() 
    {
        return operator;
    }
    public void setOperator(String operator) 
    {
        this.operator = operator;
    }
    
    /**
     * 值
    **/
    @JsonProperty("value")
    public String getValue() 
    {
        return value;
    }
    public void setValue(String value) 
    {
        this.value = value;
    }
    
    /**
     * 范围
    **/
    @JsonProperty("range")
    public List<String> getRange() 
    {
        return range;
    }
    public void setRange(List<String> range) 
    {
        this.range = range;
    }
    
    /**
     * IP匹配集合
    **/
    @JsonProperty("matchList")
    public List<String> getMatchList() 
    {
        return matchList;
    }
    public void setMatchList(List<String> matchList) 
    {
        this.matchList = matchList;
    }
    
    /**
     * 并集
    **/
    @JsonProperty("and")
    public Map<String, List<FilterItem>> getAnd() 
    {
        return and;
    }
    public void setAnd(Map<String, List<FilterItem>> and) 
    {
        this.and = and;
    }
    
    /**
     * 交集
    **/
    @JsonProperty("or")
    public Map<String, List<FilterItem>> getOr() 
    {
        return or;
    }
    public void setOr(Map<String, List<FilterItem>> or) 
    {
        this.or = or;
    }
    
    
       
    
       
    
       
    
       

       
}
