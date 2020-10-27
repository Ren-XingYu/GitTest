"use strict";

/**
*
* IP v4地址范围：
* 0.0.0.0 ~ 255.255.255.255
*
* IP地址划分:
* A类地址：1.0.0.1 ~ 126.255.255.254 
* B类地址：128.0.0.1 ~ 191.255.255.254
* C类地址：192.0.0.0 ~ 223.255.255.255
* D类地址：224.0.0.1 ~ 239.255.255.255
* E类地址：240.0.0.1 ~ 255.255.255.254
*/

const IP_V4_RGX = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
const IP_V6_RGX = /^([A-F0-9%\/]{1,4}:){7}([A-F0-9%\/]{1,4})$/i;
const IP_V6_TXT_RGX = /^[a-fA-F0-9:]+$/;
const MAC_RGX = /^((([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2})|(([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2})|(([A-Fa-f0-9]{4}-){2}[A-Fa-f0-9]{4})|(([A-Fa-f0-9]{4}:){2}[A-Fa-f0-9]{4}))$/;
const IP_V6_SEGM_RGX = /^([A-F0-9]{1,4})$/i;
const MIN_IP_V4_POINT = 0;
const MAX_IP_V4_POINT = Math.pow(2, 8) - 1;
const MIN_IP_V6_POINT = 0;
const MAX_IP_V6_POINT = Math.pow(2, 16) - 1;

/**
 * 空检测(验证通过)
 */
const checkNull = function(val){
    return val == null || val.trim() === "";
}

/**
 * 补齐(验证通过)
 */
const completeChar = function(num, char) {
    if (num < 0) {
        return "";
    }
    return new Array(num + 1).join(char);
};

/**
 * 二进制转十六进制(验证通过)
 */
const binToHex = function(bin){
    return parseInt(bin, 2).toString(16).toLowerCase();
}

/**
 * 十六进制转二进制(验证通过)
 */
const hexToBin = function(hex){
    let bin = parseInt(hex, 16).toString(2);
    return completeChar(4 - bin.length, "0") + bin;
}

/**
 * V4位码校验(验证通过)
 */
const isV4SegmValid = function(point){
    var segm = point * 1;
    return !isNaN(segm) && segm >= MIN_IP_V4_POINT && segm <= MAX_IP_V4_POINT;
};

/**
 * V6位码校验(验证通过)
 */
const isV6SegmValid = function(point){
    if (!IP_V6_SEGM_RGX.test(point)){
        return false;
    }
    var segm = parseInt(point, 16);
    return !isNaN(segm) && segm >= MIN_IP_V6_POINT && segm <= MAX_IP_V6_POINT;
};

/**
 * 子网掩码校验(测试通过)
 */
const isIpv4MaskValid = function(mask){
    mask = parseInt(mask);
    return mask >= 0 && mask <= 32
};

/**
 * 子网掩码校验
 */
const isIpv6MaskValid = function(mask){
    mask = parseInt(mask);
    return mask >= 0 && mask <= 128
};

/**
 * IPV4 基础格式校验(测试通过)
 */
const isIpv4 = function(ipStr){
    return IP_V4_RGX.test(ipStr);
};

/**
 * IPV6 基础格式校验
 */
// todo
const isIpv6 = function(ipAddr){
    if (checkNull(ipAddr) || !IP_V6_TXT_RGX.test(ipAddr)) {
        return false;
    }
    return IP_V6_RGX.test(convertIpv6(ipAddr));
};

/**
 * IP地址混检(验证通过)
 */
const isIpAddr = function(ipAddr){
    return isIpv4(ipAddr) || isIpv6(ipAddr);
}

/**
 * 判断两个IP地址是否是同一个网段中
 * 方式：IP地址分别与子网掩码做与运算，得到的结果一网络号，如果网络号相同，就在同一子网，否则，不在同一子网
 * 如果没有进行子网划分，按照IP地址划分获取默认子网掩码（当前不实现）
 *   A类  255.0.0.0
 *   B类  255.255.0.0
 *   C类  255.255.255.0
 *   缺省 255.255.255.0
 */
const checkSameSegment = function(ipAddrA, ipAddrB, maskAddr){
    if(!ipAddrA || !ipAddrB || !maskAddr){ 
        return false; 
    } 
    var maskArray = maskAddr.split(".");
    var segmOperate = function(point, index){
        return parseInt(point) & parseInt(maskArray[index]);
    };
    return ipAddrA.split(".").map(segmOperate).join(".") === ipAddrB.split(".").map(segmOperate).join(".");
};

/**
 * 段位补零
 * 如果段位为空或者占位符，则不执行补零操作 FFFF:
 */
const addZeroPoint = function(addr, char){
    if (addr == "") {
        return ["0000"];
    }
    return addr && addr.split(":").map(function(point, index){
        return  char === point || point === "" ? char : completeChar(4 - point.length, "0") + point;
    });
};

/**
 * IPV6 格式转换完整格式
 * 段位补零、压缩还原
 */
const convertIpv6 = function(ipv6Addr, char){
    if (checkNull(ipv6Addr)) {
        return null;
    }
    var ipv6Array = ipv6Addr.split("::");
    if (ipv6Array.length > 2 || ipv6Array.length == 0) {
        return null;
    }
    
    var leftArray = addZeroPoint(ipv6Array[0], char);
    var rightArray = addZeroPoint(ipv6Array[1], char);
    var middleArray = [];
    if (rightArray) {
        var zeroLen = 8 - leftArray.length - rightArray.length;
        for (var i = 0; i < zeroLen; i++) {
            middleArray.push(completeChar(4, "0"));
        }
    }
    return leftArray.concat(middleArray).concat(rightArray || []).join(":");
};

/**
 * IPV4 模糊条件 格式校验 *按段位匹配
 */
const checkIpv4Fuzzy = function(ipAddr){
    if (checkNull(ipAddr)) {
        return false;
    }
    var points = ipAddr.trim().split(".");
    return points.length <= 4 && !points.some(function(point){
        return point != "" && point != "*" && !isV4SegmValid(point);
    });
};

/**
 * IPV6 模糊条件 格式校验 *按段位匹配
 */
const checkIpv6Fuzzy = function(ipAddr){
    if (checkNull(ipAddr) || ipAddr === ":") {
        return false;
    }
    ipAddr = convertIpv6(ipAddr, "*");
    var points = ipAddr.trim().split(":");
    return points.length <= 8 && !points.some(function(point){
        return point != "" && point != "*"  && !isV6SegmValid(point);
    });
};

/**
 * IP地址格式校验，支持V4V6混检，基于“:”区别V4、V6
 * 仅存: 视为非法
 */
const checkIpFuzzy = function(ipAddr){
    if (ipAddr && ipAddr.indexOf(":") != -1) { //V6
        return checkIpv6Fuzzy(ipAddr);
    } else { //V4
        return checkIpv4Fuzzy(ipAddr);
    }
};

/**
 * MAC地址校验
 */
const isMac = function(macStr){
    if (checkNull(macStr)) {
        return false;
    }
    return MAC_RGX.test(macStr);
}

/**
 * MAC地址归一
 * 统一转换成6段式 统一使用“-”作为连接符
 */
const convertMac = function(macStr){
    return isMac(macStr) && macStr.split(/:|-/).map(function(item){
        return item.length == 4 ? item.substring(0, 2) + "-" + item.substring(2) : item;
    }).join("-");
}

/**
 * 数字转IPV4(测试通过)
 */
const convertLongToIpv4 = function(ipv4Long){
    return [ipv4Long >>> 24, ipv4Long >>> 16 & 0xFF, ipv4Long >>> 8 & 0xFF, ipv4Long & 0xFF].join('.')
};

/**
 * IPV4 转换数字(测试通过)
 */
const convertIpv4ToLong = function(ipv4Addr){
    if (!isIpv4(ipv4Addr)) {
        return -1;
    }
    let ipNum = 0;
    ipv4Addr.split(".").forEach(function(point, index){
        ipNum += parseInt(point) << (8 * (3 - index));
    });
    return ipNum >>> 0;
}

/**
 * IPV4 网段转换数字(测试通过)
 */
const getIpv4SegmentRange = function(ipv4Seg){
    let ipArr = (ipv4Seg || "").split("~");
    if (ipArr.length != 2 || !isIpv4(ipArr[0]) || !isIpv4(ipArr[1])) {
        return null;
    }
    return [convertIpv4ToLong(ipArr[0]), convertIpv4ToLong(ipArr[1])]
}

/**
 * IPV4子网转换数字(测试通过)
 */
const getIpv4SubnetRange = function(ipv4Subnet){
    let array = (ipv4Subnet || "").split("/");
    let ipAddr = array[0];
    let mask = parseInt(array[1]);
    if (!isIpv4(ipAddr) || !isIpv4MaskValid(mask)) {
        return null;
    }
    let ipLong = convertIpv4ToLong(ipAddr);
    let startIp = (ipLong & (0xFFFFFFFF << (32 - mask))) >>> 0;
    let endIp = startIp + (0xFFFFFFFF >>> mask);
    return [startIp, endIp];
}
/**
 * 创建IPV6字符串
 */
const createIpV6 = function(ipBin){
    var hex = '';
    var binArr = ipBin.match(/\w{1,4}/g);
    return binArr.map(function(bin, index){
        return (index > 0 && index % 4 == 0 ? ":" : "") + binToHex(bin)
    }).join("");
}

/**
 * IPV6 网段转换数字
 */
const getIpv6SegmentRange = function(ipv6Seg){
    let ipArr = (ipv6Seg || "").split("~");
    if (ipArr.length != 2 || !isIpv6(ipArr[0]) || !isIpv6(ipArr[1])) {
        return null;
    }
    return [convertIpv6(ipArr[0]), convertIpv6(ipArr[1])]
}

/**
 * IPV6子网转换
 */
const getIpv6SubnetRange = function(ipv6Subnet){
    let array = (ipv6Subnet || "").split("/");
    let ipAddr = convertIpv6(array[0]);
    let mask = parseInt(array[1]);
    if (!isIpv6(ipAddr) || !isIpv6MaskValid(mask)) {
        return null;
    }
    let ipBin = "";
    ipAddr.split(":").forEach(function(seg){
        seg.split("").forEach(function(hex){
            ipBin += hexToBin(hex);
        });
    });
    let baseBin = ipBin.substring(0, mask);
    let startIp = createIpV6(baseBin + completeChar(128 - baseBin.length, "0")); // 补0
    let endIp = createIpV6(baseBin + completeChar(128 - baseBin.length, "1")); // 补1
    return [startIp, endIp];
}


const utils = {
    convertIpv6: convertIpv6,
    convertMac: convertMac,
    convertLongToIpv4: convertLongToIpv4,
    convertIpv4ToLong: convertIpv4ToLong,
    getIpv4SegmentRange: getIpv4SegmentRange,
    getIpv4SubnetRange: getIpv4SubnetRange,
    getIpv6SegmentRange: getIpv6SegmentRange,
    getIpv6SubnetRange: getIpv6SubnetRange,

    isIpv4: isIpv4,
    isIpv6: isIpv6,
    isIpAddr: isIpAddr,
    isMac: isMac,
    checkIpv4Fuzzy: checkIpv4Fuzzy,
    checkIpv6Fuzzy: checkIpv6Fuzzy,
    checkIpFuzzy: checkIpFuzzy,
    checkSameSegment: checkSameSegment
};
window.fabric = window.fabric || {};
window.fabric.utils = window.fabric.utils || {};
window.fabric.utils.IpAddrUtil = utils;
module.exports = utils;
