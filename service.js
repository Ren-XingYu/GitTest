var NTAIPGroupTableService = function(CONST){
    var BaseQuery = window.fabric.utils.BaseQuery;
    const ipAddrUtil = window.fabric.utils.IpAddrUtil;
    const commonUtil = window.fabric.utils.CommonUtil;

    var getTableQuery = function(params){
        var timeRange = params.timeRange || {};
        var advFilters = params.advFilters || {};
        var dimensions = [];
        var measures = [];
        (params.columnList || []).forEach(function(column){
            if (column.type == "dimension") {
                (column.fields || [column.key]).forEach(function(dVal){
                    dimensions.indexOf(dVal) == -1 && dimensions.push(dVal);
                });
            } else if (column.type == "measure") {
                (column.fields || [column.key]).forEach(function(mVal){
                    measures.indexOf(mVal) == -1 && measures.push(mVal);
                });
            }
        });
        return {
            queryType: "basic",
            startTime: timeRange.dateFrom,
            endTime: timeRange.dateTo,
            measures: measures,
            dimensions: dimensions,
            resCategory: params.resCategory || "nta_flow_host_own"
        };
    };

    var parseIpRules = function(ipRules){
        var ipv4Ranges = [];
        var ipv6Ranges = [];
        (ipRules || []).forEach(function(ipRule){
            // 根据是否有冒号判断是IPv4还是IPv6地址
            if (ipRule.indexOf(":") != -1) { // IPV6
                var ipv6Range;
                if (ipRule.indexOf("~") != -1) { // 范围
                    ipv6Range = ipAddrUtil.getIpv6SegmentRange(ipRule);
                } else if (ipRule.indexOf("/") != -1) { // 子网
                    ipv6Range = ipAddrUtil.getIpv6SubnetRange(ipRule);
                } else if (ipAddrUtil.isIpv6(ipRule)) {
                    ipv6Range = ipAddrUtil.convertIpv6(ipRule); // 其他的情况
                }
                ipv6Range && ipv6Ranges.push(ipv6Range);
            } else { //IPV4
                var ipv4Range;
                if (ipRule.indexOf("~") != -1) {  // 范围
                    ipv4Range = ipAddrUtil.getIpv4SegmentRange(ipRule);
                } else if (ipRule.indexOf("/") != -1) { // 子网
                    ipv4Range = ipAddrUtil.getIpv4SubnetRange(ipRule);
                } else if (ipAddrUtil.isIpv4(ipRule)) { // 其他情况
                    ipv4Range = ipAddrUtil.convertIpv4ToLong(ipRule);
                }
                ipv4Range && ipv4Ranges.push(ipv4Range);
            }
        });
        return {
            ipv4Ranges: ipv4Ranges,
            ipv6Ranges: ipv6Ranges
        };
    };

    var parseFilters = function(ipv4Ranges, ipv6Ranges){
        var filters = {};
        if (ipv4Ranges && ipv4Ranges.length > 0) {
            var ipv4Items = [];
            ipv4Ranges.forEach(function(ipv4Range){
                if (Array.isArray(ipv4Range)) {
                    ipv4Items.push({
                        operator: "range",
                        range: ipv4Range
                    });
                } else {
                    ipv4Items.push({
                        operator: "equals",
                        value: ipv4Range
                    });
                }
            });
            console.log("ipV4Items=",ipv4Items)
            filters.host_is_ipv4 = filters.host_is_ipv4 || [];
            filters.host_is_ipv4.push({
                operator: "equals",
                value: "1",
                and: {
                    host_long: ipv4Items
                }
            });
        }
        if (ipv6Ranges && ipv6Ranges.length > 0) {
            var ipv6Items = [];
            ipv6Ranges.forEach(function(ipv6Range){
                if (Array.isArray(ipv6Range)) {
                    ipv6Items.push({
                        operator: "range",
                        range: ipv6Range
                    });
                } else {
                    ipv6Items.push({
                        operator: "equals",
                        value: ipv6Range
                    });
                }
            });
            filters.host_is_ipv4 = filters.host_is_ipv4 || [];
            filters.host_is_ipv4.push({
                operator: "equals",
                value: "0",
                and: {
                    host_addr: ipv6Items
                }
            });
        }
        console.log("filters=",filters)
        return filters;
    };

    var checkInGroup = function(ipGroup, hostAddr) {
        if (ipAddrUtil.isIpv4(hostAddr)) {
            let hostLong = ipAddrUtil.convertIpv4ToLong(hostAddr);
            return (ipGroup.ipv4Ranges || []).some(function(range){
                if (Array.isArray(range)) {
                    return hostLong >= range[0] && hostLong <= range[1];
                } else {
                    return hostLong === range;
                }
            });
        } else if (ipAddrUtil.isIpv6(hostAddr)){
            let ipv6 = ipAddrUtil.convertIpv6(hostAddr);
            return (ipGroup.ipv6Ranges || []).some(function(range){
                if (Array.isArray(range)) {
                    return ipv6 >= range[0] && ipv6 <= range[1];
                } else {
                    return ipv6 === range;
                }
            })
        }
        return false;
    }

    var NTATableQuery = BaseQuery.extend({
        /**
         * 请求表格数据
         */
        queryTableData: function (params, callback) {
            console.log("params=",params)
            if (Object.keys(params.advFilters).length != 0) {
                let queryFilters = {"ipgroup_name": params.advFilters["ip_group_name"][0]["value"]};
                var queryParams = {
                    pagesize: 10,
                    pagenum: params.pagenum,
                    filter: JSON.stringify(queryFilters)
                };
            } else {
                var queryParams = {
                    pagesize: 10,
                    pagenum: params.pagenum // 0
                };
            }
            console.log("queryParams=",queryParams)
            let that = this;
            var queryHandler = function (groupResult) {  // IP组回调函数
                console.log("groupResult=",groupResult)
                let table_datas = groupResult.table_datas;// 获取到IP组信息
                let ipv4Ranges = [];
                let ipv6Ranges = [];
                let ipGroups = (table_datas || []).map(function(tableDataItem){ // 对每一个组进行循环
                    let ipRange = parseIpRules((tableDataItem.iprange_rules || []).map(function(iprangeRuleItem){
                        //console.log("iprangeRuleItem=",iprangeRuleItem)
                        //console.log("html Encode iprangeRuleItem=",commonUtil.htmlDecode(iprangeRuleItem.iprule_content))
                        return commonUtil.htmlDecode(iprangeRuleItem.iprule_content); // 对每一个IP进行循环
                    }));
                    ipv4Ranges = ipv4Ranges.concat(ipRange.ipv4Ranges);
                    ipv6Ranges = ipv6Ranges.concat(ipRange.ipv6Ranges);
                    return {
                        groupId: tableDataItem.ipgroup_id,
                        groupName: tableDataItem.ipgroup_name,
                        ipv4Ranges: ipRange.ipv4Ranges,
                        ipv6Ranges: ipRange.ipv6Ranges,
                    };
                });
                console.log("ipGroups=",ipGroups);
                let metricsQueryParams = getTableQuery(params);
                console.log("metricsQueryParams before=",metricsQueryParams)
                metricsQueryParams["advFilters"] = parseFilters(ipv4Ranges, ipv6Ranges);
                metricsQueryParams["dimensions"] = ["host_addr"];
                console.log("metricsQueryParams after=",metricsQueryParams)
                var metricsQueryHandler = function (result) {
                    var dataset = [];
                    var measCols = params.columnList.filter(function(col){
                        return col.type == "measure";
                    });
                    ipGroups.forEach(function(ipGroup, index){
                        var rowData = {
                            ntaExpand: {
                                expanded: queryParams.pagenum > 1 ? false : index == 0
                            },
                            cateId: params.cateId,
                            timeRange: params.timeRange,
                            views: params.views
                        };
                        ((result || []).resultData || []).forEach(function(item, index){
                            var measureData = item["measureData"][0] || {};
                            if (checkInGroup(ipGroup, item["host_addr"])) {
                                measCols.forEach(function(mCol){
                                    if (rowData[mCol.key]) {
                                        rowData[mCol.key] += (measureData[mCol.key] * 1);
                                    } else {
                                        rowData[mCol.key] = (measureData[mCol.key] * 1);
                                    }
                                });
                            }
                        });
                        rowData["rowid"] = ipGroup.groupId;
                        rowData["ip_group_name"] = commonUtil.htmlDecode(ipGroup.groupName);
                        rowData["subAdvFilters"] = parseFilters(ipGroup.ipv4Ranges, ipGroup.ipv6Ranges);;
                        dataset.push(rowData);
                    });
                    callback({
                        dataset: dataset,
                        totalCount: table_datas.length || 0
                    })
                }
                if (ipv4Ranges.length == 0 && ipv6Ranges.length == 0) {
                    metricsQueryHandler();
                } else {
                    that.queryPost(CONST.QUERY_TBL_URL, JSON.stringify(metricsQueryParams), metricsQueryHandler, metricsQueryHandler);
                }
            }
            this.queryGet(CONST.QUERY_IPGROUP_TBL_URL, queryParams, queryHandler, queryHandler);
        },
    });
    
    return new NTATableQuery();
};
NTAIPGroupTableService.$inject = ["CONST"];
module.exports = NTAIPGroupTableService;
