package web.domain;

import lombok.Data;

import java.util.Date;

@Data
public class UserHistory {
    private Long uid;
    private String businessType;
    private String playType;
    private String objectId;
    private String objectName;
    private Long startTime;
    private Long endTime;
    private String assortId;
    private String lastProgramId;
    private String lastProgramName;
    private Long startWatchTime;
    private String bannerImg;
    private String verticalImg;
    private String directors;
    private String actors;
    private String deviceGroupId;
    private String templateId;
    private String vendor;
    private Long seconds;
    private Long totalTime;
    private Long seriesNumber;
    private String logType;
    private Date updateTime;
    private String deviceType;
    private String titleData;
}
