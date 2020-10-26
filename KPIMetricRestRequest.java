

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
 * KPI查询请求模型基类。
 **/
 @JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class KPIMetricRestRequest    
{
    
    @Valid
    @JsonProperty("resCategory")
    private String resCategory = null;
    
    @Valid
    @JsonProperty("tenantId")
    private String tenantId = null;
    
    @Valid
    @JsonProperty("siteIds")
    private List<String> siteIds = new ArrayList<String>();
    
    @Valid
    @JsonProperty("startTime")
    private Long startTime = null;
    
    @Valid
    @JsonProperty("endTime")
    private Long endTime = null;
    
    public enum QueryTypeEnum 
    { 
         @JsonProperty("topn") TOPN,  @JsonProperty("count") COUNT,  @JsonProperty("sequence") SEQUENCE,  @JsonProperty("limit_sequence") LIMIT_SEQUENCE,  @JsonProperty("list") LIST,  @JsonProperty("basic") BASIC,  @JsonProperty("metric") METRIC,  @JsonProperty("metric_sequence") METRIC_SEQUENCE, ; 
    };
    
    @JsonProperty("queryType")
    private QueryTypeEnum queryType = null;
    
    @Valid
    @JsonProperty("measures")
    private List<String> measures = new ArrayList<String>();
    
    @Valid
    @JsonProperty("dimensions")
    private List<String> dimensions = new ArrayList<String>();
    
    @Valid
    @JsonProperty("statsBy")
    private List<String> statsBy = new ArrayList<String>();
    
    @Valid
    @JsonProperty("orderBy")
    private String orderBy = "desc";
    
    @Valid
    @JsonProperty("orderKey")
    private String orderKey = null;
    
    @Valid
    @JsonProperty("queryId")
    private String queryId = null;
    
    @Valid
    @JsonProperty("limit")
    private Integer limit = null;
    
    @Valid
    @JsonProperty("offset")
    private Integer offset = 0;
    
    @Valid
    @JsonProperty("showOther")
    private Boolean showOther = false;
    
    @Valid
    @JsonProperty("filters")
    private Map<String, List<String>> filters = new HashMap<String, List<String>>();
    
    @Valid
    @JsonProperty("advFilters")
    private Map<String, List<FilterItem>> advFilters = new HashMap<String, List<FilterItem>>();
    
    
    /**
     * 查找配置所需资源种类
    **/
    @JsonProperty("resCategory")
    public String getResCategory() 
    {
        return resCategory;
    }
    public void setResCategory(String resCategory) 
    {
        this.resCategory = resCategory;
    }
    
    /**
     * 租户id
    **/
    @JsonProperty("tenantId")
    public String getTenantId() 
    {
        return tenantId;
    }
    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }
    
    /**
     * 租户可访问site id
    **/
    @JsonProperty("siteIds")
    public List<String> getSiteIds() 
    {
        return siteIds;
    }
    public void setSiteIds(List<String> siteIds) 
    {
        this.siteIds = siteIds;
    }
    
    /**
     * 查询起始时间
    **/
    @JsonProperty("startTime")
    public Long getStartTime() 
    {
        return startTime;
    }
    public void setStartTime(Long startTime) 
    {
        this.startTime = startTime;
    }
    
    /**
     * 查询终止时间
    **/
    @JsonProperty("endTime")
    public Long getEndTime() 
    {
        return endTime;
    }
    public void setEndTime(Long endTime) 
    {
        this.endTime = endTime;
    }
    
    /**
     * KPI查询类型
    **/
    @JsonProperty("queryType")
    public QueryTypeEnum getQueryType() 
    {
        return queryType;
    }
    public void setQueryType(QueryTypeEnum queryType) 
    {
        this.queryType = queryType;
    }
    
    /**
     * KPI查询请求度量
    **/
    @JsonProperty("measures")
    public List<String> getMeasures() 
    {
        return measures;
    }
    public void setMeasures(List<String> measures) 
    {
        this.measures = measures;
    }
    
    /**
     * KPI查询请求维度
    **/
    @JsonProperty("dimensions")
    public List<String> getDimensions() 
    {
        return dimensions;
    }
    public void setDimensions(List<String> dimensions) 
    {
        this.dimensions = dimensions;
    }
    
    /**
     * KPI查询统计维度
    **/
    @JsonProperty("statsBy")
    public List<String> getStatsBy() 
    {
        return statsBy;
    }
    public void setStatsBy(List<String> statsBy) 
    {
        this.statsBy = statsBy;
    }
    
    /**
     * 查询排序方式
    **/
    @JsonProperty("orderBy")
    public String getOrderBy() 
    {
        return orderBy;
    }
    public void setOrderBy(String orderBy) 
    {
        this.orderBy = orderBy;
    }
    
    /**
     * 排序度量
    **/
    @JsonProperty("orderKey")
    public String getOrderKey() 
    {
        return orderKey;
    }
    public void setOrderKey(String orderKey) 
    {
        this.orderKey = orderKey;
    }
    
    /**
     * 查询指定对象时所需id
    **/
    @JsonProperty("queryId")
    public String getQueryId() 
    {
        return queryId;
    }
    public void setQueryId(String queryId) 
    {
        this.queryId = queryId;
    }
    
    /**
     * 查询对象数量
    **/
    @JsonProperty("limit")
    public Integer getLimit() 
    {
        return limit;
    }
    public void setLimit(Integer limit) 
    {
        this.limit = limit;
    }
    
    /**
     * 用于分页，起始对象下标。
    **/
    @JsonProperty("offset")
    public Integer getOffset() 
    {
        return offset;
    }
    public void setOffset(Integer offset) 
    {
        this.offset = offset;
    }
    
    /**
     * 是否展示其他对象度量
    **/
    @JsonProperty("showOther")
    public Boolean getShowOther() 
    {
        return showOther;
    }
    public void setShowOther(Boolean showOther) 
    {
        this.showOther = showOther;
    }
    
    /**
     * 过滤条件
    **/
    @JsonProperty("filters")
    public Map<String, List<String>> getFilters() 
    {
        return filters;
    }
    public void setFilters(Map<String, List<String>> filters) 
    {
        this.filters = filters;
    }
    
    /**
     * 高级过滤条件
    **/
    @JsonProperty("advFilters")
    public Map<String, List<FilterItem>> getAdvFilters() 
    {
        return advFilters;
    }
    public void setAdvFilters(Map<String, List<FilterItem>> advFilters) 
    {
        this.advFilters = advFilters;
    }
    
    
       
    
       
    
       
    
       

       
}
