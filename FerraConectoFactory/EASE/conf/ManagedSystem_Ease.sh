#!/bin/ksh
echo "*******************************************************************************"
echo "* This script will insert the new connector to the ESS server          	    *"
echo "*******************************************************************************"
echo "Please enter ess administrator name:"
read ESS_USR
echo "Please enter ess administrator password:"
stty -echo
read ESS_PWD
stty echo
COMMAND="REFRESH rss_type WITH rss_type= \"Ease\" SET description= \"\" , predefined_type= \"0\" , active_type= \"1\" , max_pwd_len= \"128\" , pwd_reset_add_user= \"1\" , pwd_reset_chg_pwd= \"0\" , pwd_on_restore= \"0\" , requires_user_group= \"Not Supported\" , case_sensitivity= \"Ignore\" , permanent_ace= \"0\" , rss_upd_defgrp= \"Keep\" , del_ug_when= \"Not Connected to Users\" , get_conn_on_ug_add= \"0\" , get_conn_on_ug_upd= \"0\" , get_conn_on_usr_add= \"0\" , get_conn_on_usr_upd= \"0\" , get_conn_on_intrcpt_usr_upd= \"0\" , sync_user_on_conn_chg= \"0\" , get_acl_on_res_upd= \"0\" , relative_position_ace= \"0\" , rss_supports_advanced_load= \"1\" , user_define_res_type= \"0\" , rss_supports_oe= \"0\" , pw_sync_self_admin= \"0\" , names_case_sensitive= \"0\" , admin_password_required= \"1\" , user_name_type_check= \"%512s\" , user_grp_type_check= \"%512s\" , resource_type_check= \"%512s\" , resource_type_type_check= \"%512s\";SAVE_DEFINITION rules;"
echo $COMMAND > input.$$
ess batchrun -i input.$$  -U ${ESS_USR} -P ${ESS_PWD}
rm input.$$
COMMAND="REFRESH_ALL keyword WITH keyword_name= \"Ease_XSA_ACE_RESNAME\" ,entity= \"ace\" , keyword_rsstype= \"Ease\" , SET entry_type= \"User Defined\" , keyword_label= \"Ace Resource\" , data_type= \"char\" , tab= \"General\" , keyword_order= \"32\" , __99__NULL= \"N\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\",__99__ACCESS.LIMITED= \"1\",__99__MANDATORY= \"1\",__99__MANDATORY.INSERT= \"1\",__99__MANDATORY.UPDATE= \"1\",__99__MANDATORY.DELETE= \"1\", __99__READONLY_IN_UPDATE= \"1\" , __99__DIM= \"60x2\" , __99__LENGTH= \"100\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_XSA_ACE_RESTYPE\" ,entity= \"ace\" , keyword_rsstype= \"Ease\" , SET entry_type= \"User Defined\" , keyword_label= \"Ace Resource Type\" , data_type= \"char\" , tab= \"General\" , keyword_order= \"33\" , __99__NULL= \"N\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\",__99__ACCESS.LIMITED= \"1\",__99__MANDATORY= \"1\",__99__MANDATORY.INSERT= \"1\",__99__MANDATORY.UPDATE= \"1\",__99__MANDATORY.DELETE= \"1\", __99__READONLY_IN_UPDATE= \"1\" , __99__LENGTH= \"20\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_NO_USUARIO\" ,entity= \"rss_user\" , keyword_rsstype= \"Ease\" , SET entry_type= \"User Defined\" , keyword_label= \"NO_USUARIO\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"34\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_PASSWORD\" ,entity= \"rss_user\" , keyword_rsstype= \"Ease\" , SET entry_type= \"User Defined\" , keyword_label= \"PASSWORD\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"35\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_NO_PERFIL\" ,entity= \"rss_user\" , keyword_rsstype= \"Ease\" , SET entry_type= \"User Defined\" , keyword_label= \"NO_PERFIL\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"36\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_NO_LOTACAO\" ,entity= \"rss_user\" , keyword_rsstype= \"Ease\" , SET entry_type= \"User Defined\" , keyword_label= \"NO_LOTACAO\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"37\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_NO_PERFIL\" ,entity= \"user_group\" , keyword_rsstype= \"Ease\" , SET entry_type= \"User Defined\" , keyword_label= \"NO_PERFIL\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"38\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
"
echo $COMMAND > input.$$
ess batchrun -i input.$$  -S~ -s, -U ${ESS_USR} -P ${ESS_PWD}
rm input.$$
sleep 2
COMMAND="DELETE_ALL keyword WITH keyword_rsstype = \"Ease\";"
echo $COMMAND > input.$$
ess batchrun -i input.$$  -U ${ESS_USR} -P ${ESS_PWD}
rm input.$$
COMMAND="REFRESH_ALL keyword WITH keyword_name= \"Ease_XSA_ACE_RESNAME\" ,entity= \"ace\" , keyword_rsstype= \"Ease\" , SET entry_type= \"Predefined\" , keyword_label= \"Ace Resource\" , data_type= \"char\" , tab= \"General\" , keyword_order= \"32\" , __99__NULL= \"N\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\",__99__ACCESS.LIMITED= \"1\",__99__MANDATORY= \"1\",__99__MANDATORY.INSERT= \"1\",__99__MANDATORY.UPDATE= \"1\",__99__MANDATORY.DELETE= \"1\", __99__READONLY_IN_UPDATE= \"1\" , __99__DIM= \"60x2\" , __99__LENGTH= \"100\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_XSA_ACE_RESTYPE\" ,entity= \"ace\" , keyword_rsstype= \"Ease\" , SET entry_type= \"Predefined\" , keyword_label= \"Ace Resource Type\" , data_type= \"char\" , tab= \"General\" , keyword_order= \"33\" , __99__NULL= \"N\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\",__99__ACCESS.LIMITED= \"1\",__99__MANDATORY= \"1\",__99__MANDATORY.INSERT= \"1\",__99__MANDATORY.UPDATE= \"1\",__99__MANDATORY.DELETE= \"1\", __99__READONLY_IN_UPDATE= \"1\" , __99__LENGTH= \"20\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_NO_USUARIO\" ,entity= \"rss_user\" , keyword_rsstype= \"Ease\" , SET entry_type= \"Predefined\" , keyword_label= \"NO_USUARIO\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"34\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_PASSWORD\" ,entity= \"rss_user\" , keyword_rsstype= \"Ease\" , SET entry_type= \"Predefined\" , keyword_label= \"PASSWORD\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"35\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_NO_PERFIL\" ,entity= \"rss_user\" , keyword_rsstype= \"Ease\" , SET entry_type= \"Predefined\" , keyword_label= \"NO_PERFIL\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"36\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_NO_LOTACAO\" ,entity= \"rss_user\" , keyword_rsstype= \"Ease\" , SET entry_type= \"Predefined\" , keyword_label= \"NO_LOTACAO\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"37\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
REFRESH_ALL keyword WITH keyword_name= \"Ease_NO_PERFIL\" ,entity= \"user_group\" , keyword_rsstype= \"Ease\" , SET entry_type= \"Predefined\" , keyword_label= \"NO_PERFIL\" , data_type= \"text\" , tab= \"Ease Parameters\" , keyword_order= \"38\" , __99__NULL= \"Y\" , __99__ENCRYPT= \"0\" , __99__DB_DFL= \"\" , __99__DIM= \"60x2\" , __99__LENGTH= \"255\";
"
echo $COMMAND > input.$$
ess batchrun -i input.$$  -S~ -s, -U ${ESS_USR} -P ${ESS_PWD}
rm input.$$


