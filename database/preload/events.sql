USE doorbox;

DROP EVENT IF EXISTS clearExpiredEphemeralCodes;
CREATE EVENT IF NOT EXISTS clearExpiredEphemeralCodes
    ON SCHEDULE
        EVERY 1 HOUR
        COMMENT 'Removes expired EphemeralCodes after 30 days'
        DO
          DELETE FROM EPHEMERAL_CODE WHERE END_DATE < DATE_SUB(NOW(), INTERVAL 30 DAY);

DROP EVENT IF EXISTS clearSurplusActivityLogs;
CREATE EVENT IF NOT EXISTS clearSurplusActivityLogs
    ON SCHEDULE
        EVERY 1 HOUR
        COMMENT 'Removes expired Activity Logs after 90 days'
        DO
          DELETE FROM LOG WHERE TIMESTAMP < DATE_SUB(NOW(), INTERVAL 90 DAY);

