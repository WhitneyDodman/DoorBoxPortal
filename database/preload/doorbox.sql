USE doorbox;

INSERT INTO doorbox.doorbox_model(model_id, model_number, dimensions, custom, networked, insulated, features, photo_url) VALUES (1001, 'DB2590 (Small)', 'HEIGHT: (back) 22" (front) 18.5", WIDTH: 12.5", DEPTH: 11.2", ENTRY/OPENING AREA: Width: 10.4", Depth: 8.26"', false, false, false, '', '');
INSERT INTO doorbox.doorbox_model(model_id, model_number, dimensions, custom, networked, insulated, features, photo_url) VALUES (1002, 'DB5460 (Medium)', 'HEIGHT: (back) 27.5" (front) 21.6", WIDTH: 16.1", DEPTH: 15.7", ENTRY/OPENING AREA: Width: 14.5", Depth: 12.9"', false, false, false, '', '');
INSERT INTO doorbox.doorbox_model(model_id, model_number, dimensions, custom, networked, insulated, features, photo_url) VALUES (1003, 'DB6816 (Large)', 'HEIGHT: (back) 27.5" (front) 21.6", WIDTH: 20.1", DEPTH: 15.7", ENTRY/OPENING AREA: Width: 18.5", Depth: 12.9"', false, false, false, '', '');

INSERT INTO doorbox.doorbox(DOORBOX_ID, ACCOUNT_ID, UNIQUE_ID, LATITUDE, LONGITUDE, MODEL_ID, DATE_CREATED, DATE_UPDATED, INIT_DATE, MASTER_CODE, SUB_MASTER_CODE, TECHNICIAN_CODE,LOCATION_DESCRIPTION, ADDRESS_1, ADDRESS_2, CITY, STATE_PROV, COUNTRY, POSTAL_CODE, PHOTO_URL) VALUES (1001,1001,'doorbox1', '43.463894', '-79.718472', 1002, '2016-08-27 00:00:00','2016-08-27 00:00:00','2016-08-27 13:16:00','22446688', '11335577', '33557799', 'Front Porch', '2075 Oxford Ave',       NULL, 'Oakville', 'ON', 'CA', 'L6H 4K8', 'images/poc-doorbox-location1-url-thumb.jpg');
INSERT INTO doorbox.doorbox(DOORBOX_ID, ACCOUNT_ID, UNIQUE_ID, LATITUDE, LONGITUDE, MODEL_ID, DATE_CREATED, DATE_UPDATED, INIT_DATE, MASTER_CODE, SUB_MASTER_CODE, TECHNICIAN_CODE,LOCATION_DESCRIPTION, ADDRESS_1, ADDRESS_2, CITY, STATE_PROV, COUNTRY, POSTAL_CODE, PHOTO_URL) VALUES (1002,1001,'doorbox2', '43.463894', '-79.718472', 1003, '2016-08-27 00:00:00','2016-08-27 00:00:00','2016-08-27 13:16:00','22446688', '11335577', '33557799', 'Docking Bay', '2075 Oxford Ave',       NULL, 'Oakville', 'ON', 'CA', 'L6H 4K8', 'images/poc-doorbox-location2-url-thumb.jpg');
INSERT INTO doorbox.doorbox(DOORBOX_ID, ACCOUNT_ID, UNIQUE_ID, LATITUDE, LONGITUDE, MODEL_ID, DATE_CREATED, DATE_UPDATED, INIT_DATE, MASTER_CODE, SUB_MASTER_CODE, TECHNICIAN_CODE,LOCATION_DESCRIPTION, ADDRESS_1, ADDRESS_2, CITY, STATE_PROV, COUNTRY, POSTAL_CODE, PHOTO_URL) VALUES (1003,1002,'db1',      '43.664975', '-79.307927', 1002, '2016-08-27 00:00:00','2016-08-27 00:00:00','2016-08-27 13:16:00','22446688', '11335577', '33557799', 'Front Step',  '25 Sara Ashbridge Ave', NULL, 'Toronto',  'ON', 'CA', 'M4L 3Y1', 'images/poc-doorbox-location1-url-thumb.jpg');



