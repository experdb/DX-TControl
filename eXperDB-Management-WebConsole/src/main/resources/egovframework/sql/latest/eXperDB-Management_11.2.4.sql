INSERT INTO t_sysdtl_c (grp_cd, sys_cd, sys_cd_nm, use_yn, frst_regr_id, frst_reg_dtm, lst_mdfr_id, lst_mdf_dtm) VALUES ('TC0002', 'TC000203', '배치작업', 'Y', 'ADMIN', clock_timestamp(), 'ADMIN', clock_timestamp());
INSERT INTO t_sysdtl_c (grp_cd, sys_cd, sys_cd_nm, use_yn, frst_regr_id, frst_reg_dtm, lst_mdfr_id, lst_mdf_dtm) VALUES ('TC0002', 'TC000204', '데이터이행', 'Y', 'ADMIN', clock_timestamp(), 'ADMIN', clock_timestamp());


UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 관리 화면' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0056';
UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 관리 화면 조회' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0056_01';
UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 수동축소 스케일-인' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0056_02';
UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 수동확장 스케일-아웃' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0056_03';
UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 관리 상세 조회' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0056_04';
UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 관리 보안그룹 상세 조회' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0056_05';
UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 관리  수동확장 실행 팝업' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0056_06';

UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 변경이력 화면' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0057';
UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 변경이력 노드 확장/축소 실행 이력 조회' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0057_01';
UPDATE T_SYSDTL_C SET sys_cd_nm = '노드 변경이력 노드 자동 확장/축소 발생 이력 조회' WHERE grp_cd = 'TC0001' AND sys_cd = 'DX-T0057_02';

INSERT INTO t_sysdtl_c (grp_cd, sys_cd, sys_cd_nm, use_yn, frst_regr_id, frst_reg_dtm, lst_mdfr_id, lst_mdf_dtm) VALUES ('TC0001', 'DX-T0056_07', '노드 관리  수동축소 실행 팝업', 'Y', 'ADMIN', clock_timestamp(), 'ADMIN', clock_timestamp());
