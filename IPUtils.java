/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPUtils {
    interface Regex {
        String IP_V4_RGX
                = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String IP_V6_RGX = "^([A-F0-9%\\/]{1,4}:){7}([A-F0-9%\\/]{1,4})$";
        String IP_V6_TXT_RGX = "^[a-fA-F0-9:]+$";
        String MAC_RGX
                = "^((([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2})|(([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2})|(([A-Fa-f0-9]{4}-){2}[A-Fa-f0-9]{4})|(([A-Fa-f0-9]{4}:){2}[A-Fa-f0-9]{4}))$";
        String IP_V6_SEGM_RGX = "^([A-F0-9]{1,4})$";
    }

    interface IPMinMax {
        int MIN_IP_V4_POINT = 0;
        int MAX_IP_V4_POINT = (int) Math.pow(2,8)-1;
        int MIN_IP_V6_POINT = 0;
        int MAX_IP_V6_POINT = (int) Math.pow(2,16)-1;
    }

    /**
     * 空检测(验证通过)
     */
    public static boolean checkNull(String val) {
        return val == null || val.trim().equals("");
    }

    /**
     * 补齐(验证通过)
     */
    public static String completeChar(int num, String ch) {
        if (num < 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * 二进制转十六进制(验证通过)
     */
    public static String binToHex(String bin) {
        return Integer.toHexString(Integer.parseInt(bin, 2)).toLowerCase();
    }

    /**
     * 十六进制转二进制(验证通过)
     */
    public static String hexToBin(String hex) {
        String bin = Integer.toBinaryString(Integer.parseInt(hex, 16));
        return completeChar(4 - bin.length(), "0")+bin;
    }

    /**
     * V4位码校验(验证通过)
     */
    public static boolean isV4SegmValid(String point) {
        int segm = Integer.parseInt(point);
        return !Double.isNaN(segm) && segm >= IPMinMax.MIN_IP_V4_POINT && segm <= IPMinMax.MAX_IP_V4_POINT;
    }

    /**
     * V6位码校验(验证通过)
     */
    public static boolean isV6SegmValid(String point) {
        if (!testRegex(Regex.IP_V6_SEGM_RGX, point)) {
            return false;
        }
        int segm = Integer.parseInt(point, 16);
        return !Double.isNaN(segm) && segm >= IPMinMax.MIN_IP_V6_POINT && segm <= IPMinMax.MAX_IP_V6_POINT;
    }

    /**
     * V4子网掩码校验(测试通过)
     */
    public static boolean isIpv4MaskValid(String mask) {
        int mas = Integer.parseInt(mask);
        return mas >= 0 && mas <= 32;
    }

    /**
     * V6子网掩码校验(验证通过)
     */
    public static boolean isIpv6MaskValid(String mask) {
        int mas = Integer.parseInt(mask);
        return mas >= 0 && mas <= 128;
    }

    /**
     * IPV4基础校验格式(测试通过)
     */
    public static boolean isIpv4(String ipStr) {
        return testRegex(Regex.IP_V4_RGX, ipStr);
    }

    /**
     * IPV6基础格式校验
     */
    // todo
    public static boolean isIpv6(String ipAddr) {
        if (checkNull(ipAddr) || !testRegex(Regex.IP_V6_TXT_RGX, ipAddr)) {
            return false;
        }
        return testRegex(Regex.IP_V6_RGX, convertIpv6(ipAddr,""));
    }

    /**
     * IP地址混检(验证通过)
     */
    public static boolean isIpAddr(String ipAddr) {
        return isIpv4(ipAddr) || isIpv6(ipAddr);
    }

    /**
     * 判断两个IP地址是否是同一个网段中
     * 方式：IP地址分别与子网掩码做与运算，得到的结果一网络号，如果网络号相同，就在同一子网，否则，不在同一子网
     * 如果没有进行子网划分，按照IP地址划分获取默认子网掩码（当前不实现）
     * A类  255.0.0.0
     * B类  255.255.0.0
     * C类  255.255.255.0
     * 缺省 255.255.255.0
     */
    public static boolean checkSameSegment(String ipAddrA, String ipAddrB, String maskAddr) {
        if (checkNull(ipAddrA) || checkNull(ipAddrB) || checkNull(maskAddr)) {
            return false;
        }
        String[] ipAddrAArray = ipAddrA.split("\\.");
        String[] ipAddrBArray = ipAddrB.split("\\.");
        String[] maskArray = maskAddr.split("\\.");
        for (int i = 0; i < ipAddrAArray.length; i++) {
            int ipA = Integer.parseInt(ipAddrAArray[i]);
            int ipB = Integer.parseInt(ipAddrBArray[i]);
            int mask = Integer.parseInt(maskArray[i]);
            if ((ipA & mask) != (ipB & mask)) {
                return false;
            }
        }
        return true;

    }

    /**
     * 段位补零
     * 如果段位为空或者占位符，则不执行补零操作 FFFF:
     */
    public static String[] addZeroPoint(String addr, String ch) {
        if (addr.equals("")) {
            return new String[]{"0000"};
        }
        String[] addrs = addr.split("\\.");
        for (int i = 0; i < addr.length(); i++) {
            addrs[i] = (ch.equals(addrs[i]) || addrs[i].equals(""))
                    ? ch
                    : completeChar(4 - addrs[i].length(), "0") + addrs[i];
        }
        return addrs;
    }

    public static boolean testRegex(String patterns, String str) {
        return Pattern.matches(patterns, str);
    }

    /**
     * IPV6 格式转换完整格式
     * 段位补零、压缩还原
     */
    public static String convertIpv6(String ipv6Addr, String ch) {
        if (checkNull(ipv6Addr)) {
            return null;
        }
        String[] ipv6Array = ipv6Addr.split("::");
        if (ipv6Addr.length() > 2 || ipv6Array.length == 0) {
            return null;
        }
        String[] leftArray = addZeroPoint(ipv6Array[0], ch);
        List<String> leftList = Arrays.asList(leftArray);
        String[] rightArray = addZeroPoint(ipv6Array[1], ch);
        List<String> rightList = Arrays.asList(rightArray);
        List<String> middleList = new ArrayList<>();
        if (rightList.size() != 0) {
            int zeroLenght = 8 - leftList.size() - rightList.size();
            for (int i = 0; i < zeroLenght; i++) {
                middleList.add(completeChar(4, "0"));
            }
        }
        leftList.addAll(middleList);
        leftList.addAll(rightList);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftList.size(); i++) {
            if (i != leftList.size() - 1) {
                sb.append(leftList.get(i) + ":");
            } else {
                sb.append(leftList.get(i));
            }
        }
        return sb.toString();
    }

    /**
     * IPV4 模糊条件 格式校验 *按段位匹配
     */
    public static boolean checkIpv4Fuzzy(String ipAddr) {
        if (checkNull(ipAddr)) {
            return false;
        }
        String[] points = ipAddr.trim().split("\\.");
        boolean flag = false;
        for (String str : points) {
            if (!str.equals("") && !str.equals("*") && !isV4SegmValid(str)) {
                flag = true;
            }
        }
        return points.length <= 4 && flag;
    }

    /**
     * IPV6 模糊条件 格式校验 *按段位匹配
     */
    public static boolean checkIpv6Fuzzy(String ipAddr) {
        if (checkNull(ipAddr) || ipAddr.equals("")) {
            return false;
        }
        String ipv6 = convertIpv6(ipAddr, "*");
        String[] points = ipv6.trim().split(":");
        boolean flag = false;
        for (String str : points) {
            if (!str.equals("") && !str.equals("*") && !isV6SegmValid(str)) {
                flag = true;
            }
        }
        return points.length <= 8 && flag;
    }

    /**
     * IP地址格式校验，支持V4V6混检，基于“:”区别V4、V6
     * 仅存: 视为非法
     */
    public static boolean checkIpFuzzy(String ipAddr) {
        if (!ipAddr.isEmpty() && ipAddr.indexOf(":") != -1) {
            return checkIpv6Fuzzy(ipAddr);
        } else {
            return checkIpv4Fuzzy(ipAddr);
        }
    }

    /**
     * MAC地址校验
     */
    public static boolean isMac(String macStr) {
        if (checkNull(macStr)) {
            return false;
        }
        return testRegex(Regex.MAC_RGX, macStr);
    }

    /**
     * MAC地址归一
     * 统一转换成6段式 统一使用“-”作为连接符
     */
    public static String convertMac(String macStr) {
        if (isMac(macStr)) {
            String[] macArr = macStr.split(":|-");
            for (int i = 0; i < macArr.length; i++) {
                macArr[i] = macArr[i].length() == 4
                        ? macArr[i].substring(0, 2) + "-" + macArr[i].substring(2)
                        : macArr[i];
            }
            StringBuilder sb = new StringBuilder();
            List<String> mList = Arrays.asList(macArr);
            for (int i = 0; i < mList.size(); i++) {
                if (i != mList.size() - 1) {
                    sb.append(mList.get(i) + "-");
                } else {
                    sb.append(mList.get(i));
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 数字转IPV4(测试通过)
     */
    public static String convertLongToIpv4(long ipv4Long) {
        long[] arr = new long[]{ipv4Long >>> 24, ipv4Long >>> 16 & 0xFF, ipv4Long >>> 8 & 0xFF, ipv4Long & 0xFF};
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i != arr.length - 1) {
                result.append(arr[i] + ".");
            } else {
                result.append(arr[i]);
            }
        }
        return result.toString();
    }

    /**
     * IPV4 转换数字(测试通过)
     */
    public static long convertIpv4ToLong(String ipv4Addr) {
        if (!isIpv4(ipv4Addr)) {
            return -1;
        }
        long ipNum = 0;
        String[] ips = ipv4Addr.split("\\.");
        for (int i = 0; i < ips.length; i++) {
            ipNum += Integer.parseInt(ips[i]) << 8 * (3 - i);
        }
        return ipNum;
    }

    /**
     * IPV4 网段转换数字(测试通过)
     */
    public static List<Long> getIpv4SegmentRange(String ipv4Seg) {
        String[] ipArr = ipv4Seg.split("~");
        if (ipArr.length != 2 || !isIpv4(ipArr[0]) || !isIpv4(ipArr[1])) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        result.add(convertIpv4ToLong(ipArr[0]));
        result.add(convertIpv4ToLong(ipArr[1]));
        return result;
    }

    /**
     * IPV4子网转换数字(测试通过)
     */
    public static List<Long> getIpv4SubnetRange(String ipv4Subnet) {
        String[] array = ipv4Subnet.split("/");
        String ipAddr = array[0];
        int mask = Integer.parseInt(array[1]);
        if (!isIpv4(ipAddr) || !isIpv4MaskValid(mask + "")) {
            return null;
        }
        long ipLong = convertIpv4ToLong(ipAddr);
        long startIp = ipLong & (0xFFFFFFFF << (32 - mask));
        long endIp = startIp + (0xFFFFFFFF >>> mask);
        List<Long> result=new ArrayList<>();
        result.add(startIp);
        result.add(endIp);
        return result;
    }

    /**
     * 创建IPV6字符串
     */
    public static String createIpV6(String ipBin) {
        String hex = "";
        Pattern pattern = Pattern.compile("\\w{1,4}");
        Matcher matcher = pattern.matcher(ipBin);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }

        String[] binArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            binArr[i] = list.get(i);

        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < binArr.length; i++) {
            sb.append((i > 0 && i % 4 == 0 ? ":" : "") + binToHex(binArr[i]));
        }
        return sb.toString();
    }

    /**
     * IPV6 网段转换数字
     */
    public static String[] getIpv6SegmentRange(String ipv6Seg) {
        String[] ipArr = ipv6Seg.split("~");
        if (ipArr.length != 2 || !isIpv6(ipArr[0]) || !isIpv6(ipArr[1])) {
            return null;
        }
        return new String[]{convertIpv6(ipArr[0], "0"), convertIpv6(ipArr[1], "0")};
    }

    public static String[] getIpv6SubnetRange(String ipv6Subnet) {
        String[] array = ipv6Subnet.split("/");
        String ipAddr = convertIpv6(array[0], "0");
        int mask = Integer.parseInt(array[1]);
        if (!isIpv6(ipAddr) || !isIpv6MaskValid(mask + "")) {
            return null;
        }
        String ipBin = "";
        String[] ips = ipAddr.split(":");
        for (String str : ips) {
            String[] temp = str.split("");
            for (String s : temp) {
                ipBin += hexToBin(s);
            }
        }
        String baseBin = ipBin.substring(0, mask);
        String startIp = createIpV6(baseBin + completeChar(128 - baseBin.length(), "0")); // 补0
        String endIp = createIpV6(baseBin + completeChar(128 - baseBin.length(), "1")); // 补1
        return new String[]{startIp, endIp};

    }

}
