package com.zxl.parser.iplocation;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ParserIpLocation {

    private static Map<IpRange, Long> ipRange2CityId = new HashMap<>();
    private static Map<String, String> countryRegionId2RegionName = new HashMap<>();
    private static Map<Long, IpLocation> cityId2Location = new HashMap<>();

    static {
        try {
            //1. 读取GeoLiteCity-Blocks.csv，解析ip段(起始ip和终止ip)和位置id的对应关系，将其放在内存Map中
            InputStream inputStream = ParserIpLocation.class.getClassLoader().getResourceAsStream("iplocation/GeoLiteCity-Blocks.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] fields = line.replace("\"", "").split(",");
                //key：IpRange
                //value：位置id
                ipRange2CityId.put(new IpRange(Long.parseLong(fields[0]), Long.parseLong(fields[1])), Long.parseLong(fields[2]));
            }

            //2. 读取region_codes.csv
            InputStream inputStream2 = ParserIpLocation.class.getClassLoader().getResourceAsStream("iplocation/region_codes.csv");
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
            while ((line = reader2.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] fields = line.replace("\"", "").split(",");
                // key：国家-区域Id
                //value：区域名称
                countryRegionId2RegionName.put(fields[0] + "-" + fields[1], fields[2]);
            }
            //3. 读取GeoLiteCity-Location.csv
            InputStream inputStream3 = ParserIpLocation.class.getClassLoader().getResourceAsStream("iplocation/GeoLiteCity-Location.csv");
            BufferedReader reader3 = new BufferedReader(new InputStreamReader(inputStream3));
            while ((line = reader3.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] fields = line.replace("\"", "").split(",");
                IpLocation ipLocation = new IpLocation();
                ipLocation.setCountry(fields[1]);
                ipLocation.setRegion(countryRegionId2RegionName.getOrDefault(fields[1] + "-" + fields[2], "-"));
                ipLocation.setCity(fields[3]);
                ipLocation.setPostalCode(fields[4]);
                ipLocation.setLatitude(fields[5]);
                ipLocation.setLongitude(fields[6]);
                cityId2Location.put(Long.parseLong(fields[0]), ipLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("init ip data error");
        }
    }

    public static IpLocation parse(String ip) {
        //1.将ip转换为Long
        long num = ip2Long(ip);
        //2.获取对应的IpLocation
        //获取ip对应的locationID
        for (Map.Entry<IpRange, Long> entry : ipRange2CityId.entrySet()) {
            if (entry.getKey().getStartIp() <= num && entry.getKey().getEndIp() >= num) {
                // 根据locationID在CityId2Location中找到对应Location
                return cityId2Location.get(entry.getValue());
            }
        }
        return null;
    }

    private static long ip2Long(String ip) {
        String[] parts = ip.trim().split("\\.");
        return Long.parseLong(parts[0]) * 256 * 256 * 256
                + Long.parseLong(parts[1]) * 256 * 256
                + Long.parseLong(parts[2]) * 256
                + Long.parseLong(parts[3]);
    }
}
