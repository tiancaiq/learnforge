package com.learnforge.media.task;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.*;
import com.learnforge.media.domain.po.Media;
import com.learnforge.media.enums.FileStatus;
import com.learnforge.media.service.IMediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PullEventTask {

    private static final String PROCEDURE_EVENT = "ProcedureStateChanged";
    private static final String UPLOAD_EVENT = "NewFileUpload";
    private static final String PROCEDURE_EVENT_FINISH = "FINISH";

    private final VodClient vodClient;
    private final IMediaService mediaService;

    @Scheduled(fixedDelay = 10000)
    public void pullEvent() {
        // 1. Prepare request parameters
        PullEventsRequest req = new PullEventsRequest();
        try {
            // 2. Send request, pull event notification
            log.debug("Prepare to pull VOD event");
            PullEventsResponse response = vodClient.PullEvents(req);
            // 3. Parse response
            EventContent[] eventSet = response.getEventSet();
            // 3.1. Traverse
            List<String> ehs = new ArrayList<>();
            for (EventContent ec : eventSet) {
                // 3.2. Get event type
                String eventType = ec.getEventType();
                // 3.3. Process event
                if (PROCEDURE_EVENT.equals(eventType)) {
                    handleProcedureStateChangeEvent(ec);
                } /*else if(UPLOAD_EVENT.equals(eventType)){
                    handleUploadEvent(ec);
                }*/
                ehs.add(ec.getEventHandle());
            }
            ConfirmEventsRequest confirmReq = new ConfirmEventsRequest();
            confirmReq.setEventHandles(ehs.toArray(new String[0]));
            vodClient.ConfirmEvents(confirmReq);
            log.info("Event processing completed");
        } catch (TencentCloudSDKException e) {
            if(e.getMessage().equals("no event")){
                log.debug("No event available");
            }else{
                log.error("VOD event processing exception", e);
            }
        }
    }

    private void handleUploadEvent(EventContent ec) {
        // 1. File upload event
        FileUploadTask fut = ec.getFileUploadEvent();
        String fileId = fut.getFileId();
        // 2. Get file details
        MediaMetaData md = fut.getMetaData();
        MediaBasicInfo info = fut.getMediaBasicInfo();
        // 3. Organize results
        Media media = new Media();
        media.setFileId(fut.getFileId());
        media.setFilename(info.getName());
        media.setMediaUrl(info.getMediaUrl());
        media.setCoverUrl(info.getCoverUrl());
        media.setDuration(md.getDuration());
        media.setSize(md.getSize());
        media.setStatus(FileStatus.UPLOADED);
        mediaService.updateMediaProcedureResult(media);
    }

    private void handleProcedureStateChangeEvent(EventContent ec) {
        // 3.3.1. Task flow status change, determine if ended
        ProcedureTask pt = ec.getProcedureStateChangeEvent();
        if (PROCEDURE_EVENT_FINISH.equals(pt.getStatus())) {
            // 3.3.2. Task flow has ended, get video metadata
            MediaMetaData md = pt.getMetaData();
            Optional<MediaProcessTaskResult> optional = Arrays.stream(pt.getMediaProcessResultSet())
                    .filter(r -> "CoverBySnapshot".equals(r.getType()))
                    .findFirst();
            String coverUrl = null;
            if (optional.isPresent()) {
                coverUrl = optional.get().getCoverBySnapshotTask().getOutput().getCoverUrl();
            }
            // 3.3.3. Save to database
            Media media = new Media();
            media.setFileId(pt.getFileId());
            media.setFilename(pt.getFileName());
            media.setMediaUrl(pt.getFileUrl());
            media.setCoverUrl(coverUrl);
            media.setDuration(md.getDuration());
            media.setSize(md.getSize());
            media.setStatus(FileStatus.PROCESSED);
            mediaService.updateMediaProcedureResult(media);
        }
    }
}
