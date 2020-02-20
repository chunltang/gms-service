package com.zs.gms.common.utils;

import com.alibaba.fastjson.JSONObject;

public class DataUtil {

    public static void pushDataToDruid(String vehicleId,String data){
        JSONObject json = JSONObject.parseObject(getParams());
        JSONObject dataJson = json.getJSONObject("spec").getJSONObject("ioConfig").getJSONObject("firehose");
        dataJson.put("data",data);
        json.put("groupId",vehicleId);
        String s = HttpUtil.sendPostJson("http://192.168.2.100:9095/console/druid/druid/indexer/v1/task", json.toJSONString());
        System.out.println(s);
    }

    public static void main(String[] args) {
        /*String data="{\"vehicleId\":10007,\"modeState\":7,\"dispState\":1,\"taskState\":6,\"runFlag\":0,\"updateTime\":\"2019-12-13 10:04:44\",\"MsgProdDevCode\":10001,\"FromVakCode\":10001,\"Year\":119,\"Month\":11,\"Day\":9,\"Hour\":10,\"Minute\":34,\"Second\":0.011000,\"LockedDeviceCode\":0,\"MonitorDataType\":0,\"VakMode\":0,\"CurrentTaskCode\":0,\"TrackCode\":0,\"VakRequestCode\":0,\"CurrentGear\":0,\"GnssState\":0,\"Longitude\":0.000000,\"Latitude\":0.000000,\"xWorld\":708632341,\"yWorld\":-1206724953,\"xLocality\":0,\"yLocality\":0,\"YawAngle\":3130,\"NavAngle\":3117,\"WheelAngle\":699,\"CurSpeed\":0,\"AddSpeed\":0,\"CountofObstacle\":0,\"RealSteerAngle\":0,\"RealSteerRotSpeed\":0,\"RealAcceleratorRate\":0,\"RealHydBrakeRate\":0,\"RealElectricFlowBrakeRate\":0,\"RealMotorState\":0,\"RealForwardBrakeState\":0,\"RealElectricBrakeState\":0,\"RealParkingBrakeState\":0,\"RealLoadBrakeState\":0,\"RealMotorRotSpeed\":0,\"RealHouseLiftRate\":0,\"RealTurnLeftlightState\":0,\"RealTurnRightlightState\":0,\"RealNearLightState\":0,\"RealContourLightState\":0,\"RealBrakeLightState\":0,\"RealEmergencyLightState\":0,\"vecObstacle\":[]}";
        JSONObject toDruid = pushDataToDruid("10001",data);
        String s = HttpUtil.sendPostJson("http://192.168.2.100:9095/console/druid/druid/indexer/v1/task", toDruid.toJSONString());
        System.out.println(s);*/
    }


    public static String getParams(){
        return "{\n" +
                "  \"type\": \"index_parallel\",\n" +
                "  \"resource\": {\n" +
                "    \"requiredCapacity\": 1\n" +
                "  },\n" +
                "  \"spec\": {\n" +
                "    \"dataSchema\": {\n" +
                "      \"dataSource\": \"test\",\n" +
                "      \"parser\": {\n" +
                "        \"type\": \"string\",\n" +
                "        \"parseSpec\": {\n" +
                "          \"format\": \"json\",\n" +
                "          \"timestampSpec\": {\n" +
                "            \"column\": \"updateTime\",\n" +
                "            \"format\": \"millis\"\n" +
                "          },\n" +
                "          \"dimensionsSpec\": {\n" +
                "            \"dimensions\": [\n" +
                "              \"updateTime\",\n" +
                "              \"dispState\",\n" +
                "              \"modeState\",\n" +
                "              \"addSpeed\",\n" +
                "              \"countofObstacle\",\n" +
                "              \"currentGear\",\n" +
                "              \"currentTaskCode\",\n" +
                "              \"curSpeed\",\n" +
                "              \"day\",\n" +
                "              \"fromVakCode\",\n" +
                "              \"gnssState\",\n" +
                "              \"hour\",\n" +
                "              \"latitude\",\n" +
                "              \"lockedDeviceCode\",\n" +
                "              \"longitude\",\n" +
                "              \"minute\",\n" +
                "              \"monitorDataType\",\n" +
                "              \"month\",\n" +
                "              \"msgProdDevCode\",\n" +
                "              \"navAngle\",\n" +
                "              \"realAcceleratorRate\",\n" +
                "              \"realBrakeLightState\",\n" +
                "              \"realContourLightState\",\n" +
                "              \"realElectricBrakeState\",\n" +
                "              \"realElectricFlowBrakeRate\",\n" +
                "              \"realEmergencyLightState\",\n" +
                "              \"realForwardBrakeState\",\n" +
                "              \"realHouseLiftRate\",\n" +
                "              \"realHydBrakeRate\",\n" +
                "              \"realLoadBrakeState\",\n" +
                "              \"realMotorRotSpeed\",\n" +
                "              \"realMotorState\",\n" +
                "              \"realNearLightState\",\n" +
                "              \"realParkingBrakeState\",\n" +
                "              \"realSteerAngle\",\n" +
                "              \"realSteerRotSpeed\",\n" +
                "              \"realTurnLeftlightState\",\n" +
                "              \"realTurnRightlightState\",\n" +
                "              \"trackCode\",\n" +
                "              \"vakMode\",\n" +
                "              \"vakRequestCode\",\n" +
                "              \"wheelAngle\",\n" +
                "              \"xLocality\",\n" +
                "              \"yawAngle\",\n" +
                "              \"year\",\n" +
                "              \"yLocality\",\n" +
                "              \"yWorld\",\n" +
                "              \"runFlag\",\n" +
                "              \"taskState\",\n" +
                "              \"vehicleId\"\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"metricsSpec\": [\n" +
                "        {\n" +
                "          \"type\": \"count\",\n" +
                "          \"name\": \"count\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"granularitySpec\": {\n" +
                "        \"type\": \"uniform\",\n" +
                "        \"segmentGranularity\": \"DAY\",\n" +
                "        \"queryGranularity\": \"MINUTE\",\n" +
                "        \"rollup\": true,\n" +
                "        \"intervals\": null\n" +
                "      },\n" +
                "      \"transformSpec\": {\n" +
                "        \"filter\": null,\n" +
                "      }\n" +
                "    },\n" +
                "    \"ioConfig\": {\n" +
                "      \"type\": \"index_parallel\",\n" +
                "      \"firehose\": {\n" +
                "        \"type\": \"inline\",\n" +
                "        \"data\": \"\"\n" +
                "      },\n" +
                "      \"appendToExisting\": true\n" +
                "    },\n" +
                "    \"tuningConfig\": {\n" +
                "      \"type\": \"index_parallel\",\n" +
                "      \"maxRowsPerSegment\": null,\n" +
                "      \"maxRowsInMemory\": 1000000,\n" +
                "      \"maxBytesInMemory\": 0,\n" +
                "      \"maxTotalRows\": null,\n" +
                "      \"numShards\": null,\n" +
                "      \"partitionsSpec\": null,\n" +
                "      \"indexSpec\": {\n" +
                "        \"bitmap\": {\n" +
                "          \"type\": \"concise\"\n" +
                "        },\n" +
                "        \"dimensionCompression\": \"lz4\",\n" +
                "        \"metricCompression\": \"lz4\",\n" +
                "        \"longEncoding\": \"longs\"\n" +
                "      },\n" +
                "      \"indexSpecForIntermediatePersists\": {\n" +
                "        \"bitmap\": {\n" +
                "          \"type\": \"concise\"\n" +
                "        },\n" +
                "        \"dimensionCompression\": \"lz4\",\n" +
                "        \"metricCompression\": \"lz4\",\n" +
                "        \"longEncoding\": \"longs\"\n" +
                "      },\n" +
                "      \"maxPendingPersists\": 0,\n" +
                "      \"forceGuaranteedRollup\": false,\n" +
                "      \"reportParseExceptions\": false,\n" +
                "      \"pushTimeout\": 0,\n" +
                "      \"segmentWriteOutMediumFactory\": null,\n" +
                "      \"maxNumConcurrentSubTasks\": 1,\n" +
                "      \"maxRetry\": 3,\n" +
                "      \"taskStatusCheckPeriodMs\": 1000,\n" +
                "      \"chatHandlerTimeout\": \"PT10S\",\n" +
                "      \"chatHandlerNumRetries\": 5,\n" +
                "      \"maxNumSegmentsToMerge\": 100,\n" +
                "      \"totalNumMergeTasks\": 10,\n" +
                "      \"logParseExceptions\": false,\n" +
                "      \"maxParseExceptions\": 2147483647,\n" +
                "      \"maxSavedParseExceptions\": 0,\n" +
                "      \"partitionDimensions\": [],\n" +
                "      \"buildV9Directly\": true\n" +
                "    }\n" +
                "  },\n" +
                "  \"context\": {\n" +
                "    \"forceTimeChunkLock\": true\n" +
                "  },\n" +
                "  \"groupId\":\"\",\n" +
                "  \"dataSource\": \"test\"\n" +
                "}";
    }
}
