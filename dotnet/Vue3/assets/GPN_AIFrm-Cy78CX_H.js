var b=Object.defineProperty;var w=(A,p,e)=>p in A?b(A,p,{enumerable:!0,configurable:!0,writable:!0,value:e}):A[p]=e;var m=(A,p,e)=>w(A,typeof p!="symbol"?p+"":p,e);var l=(A,p,e)=>new Promise((n,i)=>{var r=a=>{try{o(e.next(a))}catch(u){i(u)}},g=a=>{try{o(e.throw(a))}catch(u){i(u)}},o=a=>a.done?n(a.value):Promise.resolve(a.value).then(r,g);o((e=e.apply(A,p)).next())});import s from"./HttpHandler-Ebi1068_.js";import{MapData as I}from"./MapData-lfC2UY9r.js";import{b9 as E,bc as T,aD as h,aE as v,aB as D,aC as f,e as S}from"./entry/index-M8VErHPE-1727507756861.js";import{GroupFields as B}from"./GroupField-JFOnJiHV.js";import{SysEnumMain as M}from"./SysEnumMain-Bn9Li5_w.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EnumLab-CzismWql.js";import"./EntityOID-BVVq-i_P.js";import"./SysEnum-B89JeOjj.js";class x extends E{constructor(){super("GPN_AIFrm");m(this,"Test",`
  #### 说明
  - 为了方便测试，通过AI自动生成测试数据.
  - 用于调试，饼图、柱状图、折线图等操作.
    `);m(this,"AlertCar",`
    我要做一个车辆管理表单。
    基础信息包含车辆编号，名称，车牌号、车辆类型(轿车、工程车、清洁车)等基础字段，请提供.
    从表有如下几个，请给出每个从表的字段。
    车辆配件：
    维修记录：
    出车记录：
    `);m(this,"AlertEmp",`
    我要做一个员工管理表单。
    基础信息包含，姓名，地址，电话，民族(汉族,回族,其他)，性别(女,男)，籍贯(山东，河北...), 备注，附件，概要说明。等基础字段，
    请提供其他的字段.
    从表有如下几个，请给出每个从表的字段。
    教育经历：
    工作经历：
    项目经历：
    `);m(this,"AlertQingJia",`
    我要做一个员工请假单，请提供基础字段.
    `);m(this,"WordAlert",`
  #### 帮助
  - 字段附件，附件以字段名的形式在页面中显示; 
  #### 图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/SFTable/Img/Ath1.png "屏幕截图.png") 
  #### 数据存储
  - 附件的默认保存在web服务器上。
  - 可以保存到ftp服务器上, ftp的服务器的连接配置在全局的配置文件中。
  - 如果需要保存到数据库，就需要考虑数据库的存储与备份的问题，文件将会存储在 Sys_FrmAttachmentDB 表中。
    `);m(this,"DescAIAttrs",`
  #### 帮助
  - 我们把字段交给AI，他能够帮助您分析出来该表单的字段关系，并列表让您确认，辅助您快速准确的完成表单设计。
  - 您可以输入其他的检查要求.
  #### 内置检查内容如下.
    1. 在数值类型的字段里寻找：一个字段值是通过其他字段值通过表达式计算得来. 比如: 合计=单价*数量
    2. 在日期字段里寻找：日期字段不能输入历史日期. 
    3. 在日期字段里寻找：一个日字段大于等于另外一个日期字段.
    4. 可以在字段上增加的正则表达式. 比如：手机号，邮件，地址.
    5. 根据枚举库：把文本类型配备枚举，采用模糊匹配的模式.
    6. 根据字典库：把文本类型配备外键或者外部数据源，采用模糊匹配的模式.
    7. 在日期类型与数值类型的字段中寻找，数值=日期1-日期2 的规则. 
    8. 根据表单名称：在枚举库与字典库里寻找那些字段没有被补充上给与提示.
    `);m(this,"HelpFile",`
    #### 帮助
    - 请上传excel，word,表单图片文件，AI将会根据内容，自动生成内容.
  `);m(this,"SelectAttrs",`
  #### 帮助
  - 系统根据提示词，生成如下字段.
  #### 数据存储
  - int类型的枚举值是常用的数据类型，ccfrom是格式化的存储到数据表里.
  - 创建一个int类型的字段，用于存储枚举的数据.
    `);m(this,"MyIcons",`

    Simple Line Icons
    Font Awesome Icons
    Glyphicons
    
  icon-user
  icon-people
  icon-user-female
  icon-user-follow
  icon-user-following
  icon-user-unfollow
  icon-login
  icon-logout
  icon-emotsmile
  icon-phone
  icon-call-end
  icon-call-in
  icon-call-out
  icon-map
  icon-location-pin
  icon-direction
  icon-directions
  icon-compass
  icon-layers
  icon-menu
  icon-list
  icon-options-vertical
  icon-options
  icon-arrow-down
  icon-arrow-left
  icon-arrow-right
  icon-arrow-up
  icon-arrow-up-circle
  icon-arrow-left-circle
  icon-arrow-right-circle
  icon-arrow-down-circle
  icon-check
  icon-clock
  icon-plus
  icon-minus
  icon-close
  icon-event
  icon-exclamation
  icon-organization
  icon-trophy
  icon-screen-smartphone
  icon-screen-desktop
  icon-plane
  icon-notebook
  icon-mustache
  icon-mouse
  icon-magnet
  icon-energy
  icon-disc
  icon-cursor
  icon-cursor-move
  icon-crop
  icon-chemistry
  icon-speedometer
  icon-shield
  icon-screen-tablet
  icon-magic-wand
  icon-hourglass
  icon-graduation
  icon-ghost
  icon-game-controller
  icon-fire
  icon-eyeglass
  icon-envelope-open
  icon-envelope-letter
  icon-bell
  icon-badge
  icon-anchor
  icon-wallet
  icon-vector
  icon-speech
  icon-puzzle
  icon-printer
  icon-present
  icon-playlist
  icon-pin
  icon-picture
  icon-handbag
  icon-globe-alt
  icon-globe
  icon-folder-alt
  icon-folder
  icon-film
  icon-feed
  icon-drop
  icon-drawer
  icon-docs
  icon-doc
  icon-diamond
  icon-cup
  icon-calculator
  icon-bubbles
  icon-briefcase
  icon-book-open
  icon-basket-loaded
  icon-basket
  icon-bag
  icon-action-undo
  icon-action-redo
  icon-wrench
  icon-umbrella
  icon-trash
  icon-tag
  icon-support
  icon-frame
  icon-size-fullscreen
  icon-size-actual
  icon-shuffle
  icon-share-alt
  icon-share
  icon-rocket
  icon-question
  icon-pie-chart
  icon-pencil
  icon-note
  icon-loop
  icon-home
  icon-grid
  icon-graph
  icon-microphone
  icon-music-tone-alt
  icon-music-tone
  icon-earphones-alt
  icon-earphones
  icon-equalizer
  icon-like
  icon-dislike
  icon-control-start
  icon-control-rewind
  icon-control-play
  icon-control-pause
  icon-control-forward
  icon-control-end
  icon-volume-1
  icon-volume-2
  icon-volume-off
  icon-calendar
  icon-bulb
  icon-chart
  icon-ban
  icon-bubble
  icon-camrecorder
  icon-camera
  icon-cloud-download
  icon-cloud-upload
  icon-envelope
  icon-eye
  icon-flag
  icon-heart
  icon-info
  icon-key
  icon-link
  icon-lock
  icon-lock-open
  icon-magnifier
  icon-magnifier-add
  icon-magnifier-remove
  icon-paper-clip
  icon-paper-plane
  icon-power
  icon-refresh
  icon-reload
  icon-settings
  icon-star
  icon-symbol-female
  icon-symbol-male
  icon-target
  icon-credit-card
  icon-paypal
  icon-social-tumblr
  icon-social-twitter
  icon-social-facebook
  icon-social-instagram
  icon-social-linkedin
  icon-social-pinterest
  icon-social-github
  icon-social-google
  icon-social-reddit
  icon-social-skype
  icon-social-dribbble
  icon-social-behance
  icon-social-foursqare
  icon-social-soundcloud
  icon-social-spotify
  icon-social-stumbleupon
  icon-social-youtube
  icon-social-dropbox
  icon-social-vkontakte
  icon-social-steam 
    `);this.PageTitle="AI表单"}Init(){return l(this,null,function*(){const e=new I;e.No=this.RequestVal("FrmID"),yield e.RetrieveFromDBSources(),this.AddGroup("A","AI表单(主表)"),this.TextArea("BatchAddAttrs","批量增加字段",this.WordAlert,"提示词","电话、邮件、地址","请输入字段比如：电话、邮件、地址?"),this.Table("BatchAddAttrs.SelectedAttrs","选择字段",this.SelectAttrs,!0,this.GenerBatchAttrs);const n=`我要创建一个 ${e.Name} 表单，
    要求如下：
    1. 应用场景:无
    2. 要求字段:无
   `;this.TextArea("AIFrm","输入提示词",this.WordAlert,"提示词",n,"请点击帮助参考如何输入提示词?"),this.Table("AIFrm.SelectedAttrs","选择字段",this.SelectAttrs,!0,this.GenerAttrs),this.AddBlank("AIAttrs","字段分析",this.DescAIAttrs),this.Table("AIAttrs.FX","选择可修复的项目",this.SelectAttrs,!0,this.GenerFX),this.AddBlank("Icon","增加图标","给每个控件设置icon图标."),this.Table("Icon.Save","选择可设置的字段图标",this.SelectAttrs,!0,this.GenerIcon),this.AddBlank("Test","测试数据","为了方便测试，通过AI自动生成测试数据."),this.Table("Test.Save","存储",this.SelectAttrs,!0,this.GenerTest),this.AddGroup("Z","AI表单(从表)"),this.AddBlank("Dtl","输入提示词","为了方便测试，通过AI自动生成测试数据."),this.Table("Dtl.SelectedDtls","选择从表",this.SelectAttrs,!0,this.GenerDtls),this.Table("Dtl.SelectedDtls.Attrs","选择字段",this.SelectAttrs,!0,this.GenerDtlAttrs);const i=`
        public override Map EnMap
        {
            get
            {
                if (this._enMap != null)
                    return this._enMap;

                Map map = new Map("Port_DeptEmp", "部门人员信息");
                map.IndexField = DeptEmpAttr.FK_Dept;

                map.AddMyPK();
                map.AddTBString(DeptEmpAttr.FK_Dept, null, "部门", false, false, 1, 50, 1);
                map.AddDDLEntities(DeptEmpAttr.FK_Emp, null, "操作员", new BP.Port.Emps(), false);
                map.AddTBString(DeptEmpAttr.OrgNo, null, "组织编码", false, false, 0, 50, 50);

                this._enMap = map;
                return this._enMap;
            }
        }`,r=`package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 部门人员信息 的摘要说明。
*/
public class DeptEmp extends EntityMyPK
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	/** 
	 人员
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(DeptEmpAttr.FK_Emp);
	}
	public final void setEmpNo(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.FK_Emp, value);
		this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo());
	}
	/** 
	 部门
	*/
	public final String getDeptNo()  {
		return this.GetValStringByKey(DeptEmpAttr.FK_Dept);
	}
	public final void setDeptNo(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.FK_Dept, value);
		this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo());
	}
	public final String getOrgNo()  {
		return this.GetValStringByKey(DeptEmpAttr.OrgNo);
	}
	public final void setOrgNo(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.OrgNo, value);
	}
	public final String getStationNo()  {
		return this.GetValStringByKey(DeptEmpAttr.StationNo);
	}
	public final void setStationNo(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.StationNo, value);
	}
	public final String getStationNoT()  {
		return this.GetValStringByKey(DeptEmpAttr.StationNoT);
	}
	public final void setStationNoT(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.StationNoT, value);
	}
	public final String getDeptName()  {
		return this.GetValStringByKey(DeptEmpAttr.DeptName);
	}
	public final void setDeptName(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.DeptName, value);
	}
		///#endregion

		///#region 构造函数
	/** 
	 工作部门人员信息
	*/
	public DeptEmp()
	{
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
			return this.get_enMap();

		Map map = new Map("Port_DeptEmp", "部门人员信息");
		map.IndexField = DeptEmpAttr.FK_Dept;

		map.AddMyPK();
		map.AddTBString(DeptEmpAttr.FK_Dept, null, "部门", false, false, 1, 50, 1);
		map.AddDDLEntities(DeptEmpAttr.FK_Emp, null, "操作员", new bp.port.Emps(), false);
		map.AddTBString(DeptEmpAttr.OrgNo, null, "组织编码", false, false, 0, 50, 50);

		//For Vue3版本.
		map.AddTBString(DeptEmpAttr.DeptName, null, "部门名称(Vue3)", false, false, 0, 500, 36);
		map.AddTBString(DeptEmpAttr.StationNo, null, "岗位编号(Vue3)", false, false, 0, 500, 36);
		map.AddTBString(DeptEmpAttr.StationNoT, null, "岗位名称(Vue3)", false, false, 0, 500, 36);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeDelete() throws Exception
	{
		bp.sys.base.Glo.WriteUserLog("删除:" + this.ToJson(), "组织数据操作");
		return super.beforeDelete();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
		bp.sys.base.Glo.WriteUserLog("新建:" + this.ToJson(), "组织数据操作");
		return super.beforeInsert();
	}

	@Override
	protected void afterDelete() throws Exception
	{
		DeptEmpStations des = new DeptEmpStations();
		des.Delete("FK_Dept", this.GetValByKey("FK_Dept"), "FK_Emp", this.GetValByKey("FK_Emp"));
		super.afterDelete();
	}

	/** 
	 更新前做的事情
	 
	 @return 
	*/
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (bp.difference.SystemConfig.getCCBPMRunModel() != bp.sys.CCBPMRunModel.Single && DataType.IsNullOrEmpty(this.getOrgNo()))
		{
			this.setOrgNo(bp.web.WebUser.getOrgNo());
		}
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			if (bp.difference.SystemConfig.getCCBPMRunModel() == bp.sys.CCBPMRunModel.SAAS)
			{
				this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo().replace(this.getOrgNo() + "_",""));
			}
			else
			{
				this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo());
			}
		}
		return super.beforeUpdateInsertAction();
	}
}
`;this.AddGroup("F","实体生成代码"),this.AddBlank("CodeTSHand","生成TS代码","把当前表单生成TS实体代码.","icon-drop"),this.TextArea("CodeTSHand.Code","预览","把当前表单生成TS实体代码.","代码模板",this.GenerCodeTS2024),this.TextArea("CodeCSharp","生成C#代码","把当前表单生成C#实体代码.","代码模板",i),this.TextArea("CodeCSharp.review","代码预览","把当前表单生成TSEntity代码.","代码模板",this.GenerCodeCS),this.TextArea("CodeJava","生成Java代码","把当前表单生成Java实体代码.","代码模板",r),this.TextArea("CodeJava.review","代码预览","把当前表单生成TSEntity代码.","代码模板",this.GenerCodeJava),this.AddGroup("B","提示词参考"),this.TextArea("Demo1","车辆表单",this.WordAlert,"提示词",this.AlertCar,"请点击帮助参考如何输入提示词?"),this.Table("Demo1.SelectedAttrs","选择字段",this.SelectAttrs,!0,this.GenerAttrs),this.TextArea("Demo2","员工信息表",this.WordAlert,"提示词",this.AlertEmp,"请点击帮助参考如何输入提示词?"),this.Table("Demo2.SelectedAttrs","选择字段",this.SelectAttrs,!0,this.GenerAttrs),this.TextArea("Demo3","请假单",this.WordAlert,"提示词",this.AlertQingJia,"请点击帮助参考如何输入提示词?"),this.Table("Demo3.SelectedAttrs","选择字段",this.SelectAttrs,!0,this.GenerAttrs)})}GenerTest(){return l(this,null,function*(){const e=new s("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FrmID",this.RequestVal("FrmID"));const n=yield e.DoMethodReturnJson("AiFrm_TestDBGener");return JSON.stringify(n)})}GenerBatchAttrs(){return l(this,null,function*(){const e=new s("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FrmID",this.RequestVal("FrmID"));const n=this.RequestVal("tb1","BatchAddAttrs");e.AddPara("Words",n);const i=yield e.DoMethodReturnJson("AiFrm_GenerBatchAttrs");return JSON.stringify(i)})}GenerAttrs(){return l(this,null,function*(){const e=new s("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FrmID",this.RequestVal("FrmID"));let n=this.RequestVal("tb1","AIFrm");(n==null||n==null||n=="")&&(n=this.RequestVal("tb1","Demo1")),(n==null||n==null||n=="")&&(n=this.RequestVal("tb1","Demo2")),(n==null||n==null||n=="")&&(n=this.RequestVal("tb1","Demo3")),e.AddPara("Words",n);const i=yield e.DoMethodReturnJson("AiFrm_GenerAttrs");return JSON.stringify(i)})}GenerFX(){return l(this,null,function*(){const e=new s("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FrmID",this.RequestVal("FrmID")),e.AddPara("Words",this.RequestVal("tb1","AIAttrs"));const n=yield e.DoMethodReturnJson("CheckFrm_GenerFX");return JSON.stringify(n)})}GenerDtls(){return l(this,null,function*(){const e=new s("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FrmID",this.RequestVal("FrmID")),e.AddPara("Words",this.RequestVal("tb1","Dtl"));const n=yield e.DoMethodReturnJson("AiFrm_DtlsGener");return JSON.stringify(n)})}GenerDtlAttrs(){return l(this,null,function*(){const e=new s("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FrmID",this.RequestVal("FrmID")),e.AddPara("DtlID",this.RequestVal("tb1","Dtl.SelectedDtls"));const n=yield e.DoMethodReturnJson("AiFrm_DtlAttrs");return JSON.stringify(n)})}GenerIcon(){return l(this,null,function*(){const e=new s("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FrmID",this.RequestVal("FrmID")),e.AddPara("Words",this.RequestVal("tb1","Icon"));const n=yield e.DoMethodReturnJson("AiFrm_IconGener");return JSON.stringify(n)})}GenerCodeTS2024(){return l(this,null,function*(){const e=new I;e.No=this.RequestVal("FrmID"),yield e.Retrieve();const n=new T;yield n.Retrieve("FK_MapData",e.No,"Idx");const i=new B;yield i.Retrieve("FrmID",e.No,"Idx");let r="";for(let o=0;o<i.length;o++){const a=i[o];r+=` 	
 map.AddGroupAttr('`+a.Lab+"'); ";for(let u=0;u<n.length;u++){const t=n[u];if(t.GroupID!=a.OID)continue;let c="false";t.UIIsEnable==1&&(c="true");let d="false";t.UIIsEnable==1&&(d="true");let F="false";if(t.UIIsLine==1&&(F="true"),t.KeyOfEn=="OID"){r+=`	
 map.AddTBIntPKOID(); `;continue}if(t.KeyOfEn=="No"){r+=`	
 map.AddTBStringPK('${t.KeyOfEn}', null, '${t.Name}', ${c}, ${d}, ${t.MinLen}, ${t.MaxLen},${t.UIWidth},${F} );`;continue}if(t.MyDataType==h.AppBoolean){r+=`	
 map.AddBoolean('${t.KeyOfEn}',false, '${t.Name}',  ${c}, ${d}, ${F});`;continue}if(t.UIContralType==1&&t.MyDataType==h.AppString){const y=new v;y.No=t.UIBindKey,yield y.Retrieve(),r+=`	
  map.AddDDLSQL('${t.KeyOfEn}', null, '${t.Name}', '${y.SelectStatement}', ${c}); `;continue}if(t.UIContralType==0&&t.MyDataType==h.AppString&&!t.UIBindKey){r+=`	
 map.AddTBString('${t.KeyOfEn}', null, '${t.Name}', ${c}, ${d}, ${t.MinLen}, ${t.MaxLen},${t.UIWidth},${F});`;continue}if(t.UIContralType==0&&t.MyDataType==h.AppInt){r+=`	
 map.AddTBInt('${t.KeyOfEn}', 0, '${t.Name}',  ${c}, ${d});`;continue}if(t.UIContralType==0&&t.MyDataType==h.AppFloat){r+=`	
 map.AddTBFloat('${t.KeyOfEn}', 0, '${t.Name}',  ${c}, ${d});`;continue}if(t.UIContralType==0&&t.MyDataType==h.AppMoney){r+=`	
 map.AddTBMoney('${t.KeyOfEn}', 0, '${t.Name}',  ${c}, ${d});`;continue}if(t.MyDataType==h.AppDate){r+=`	
 map.AppTBDate('${t.KeyOfEn}', null, '${t.Name}',  ${c}, ${d});`;continue}if(t.MyDataType==h.AppDateTime){r+=`	
 map.AppTBDateTime('${t.KeyOfEn}', null, '${t.Name}',  ${c}, ${d});`;continue}if(t.LGType==1){const y=new M(t.UIBindKey);(yield y.RetrieveFromDBSources())==1?r+=` map.AddDDLSysEnum('${t.KeyOfEn}', 0, '${t.Name}',  ${c}, ${d}, '${t.UIBindKey}', '${y.CfgVal}');`:r+=`	
 map.AddDDLSysEnum('${t.KeyOfEn}', 0, '${t.Name}',  ${c}, ${d}, '${t.UIBindKey}', '');`;continue}r+=`	
 map.AddTBString('${t.KeyOfEn}', null, '${t.Name}', ${c}, ${d}, ${t.MinLen}, ${t.MaxLen},${t.UIWidth},${F});`}}return`
    import { EntitiesOID, EntityOID } from '/@/bp/en/EntityOID';
import { UAC } from '/@/bp/en/Map/UAC';
import { Map } from '/@/bp/en/Map/Map';
     /** ${e.Name} **/
export class ${e.No} extends EntityOID {
  constructor(pkval?: string) {
   super('TS.Demo.${e.No}');
   if (!!pkval) this.setPKVal(pkval);
 }
//实体的权限控制
public override get HisUAC() {
  const uac = new UAC();
  uac.OpenForAdmin();
 // if (1==2) {
  //  uac.IsDelete = true;
   // uac.IsUpdate = true;
   // uac.IsInsert = true;
 // } 
  return uac;
}
  public override get EnMap() {
    const map = new Map('${e.PTable}', '${e.Name}');
     map.CodeStruct = '3';
     map.GroupBarShowModel = 1;
       ${r}
     this._enMap = map;
    return this._enMap;
    }
  }
     /** ${e.Name} **/
  export class ${e.No}s extends EntitiesOID {
    get GetNewEntity(): EntityOID {
      return new ${e.No}();
    }
    constructor() {
      super();
    }
  }`})}GenerCodeTS(){return l(this,null,function*(){const e="javascript",n=this.RequestVal("tb1","CodeTS"),i=new s("BP.WF.HttpHandler.WF_Admin_AI");return i.AddPara("FrmID",this.RequestVal("FrmID")),i.AddPara("Words",n),i.AddPara("code",e),yield i.DoMethodReturnString("CodeGener_Save")})}GenerCodeCS(){return l(this,null,function*(){const e="csharp",n=this.RequestVal("tb1","CodeCS"),i=new s("BP.WF.HttpHandler.WF_Admin_AI");return i.AddPara("FrmID",this.RequestVal("FrmID")),i.AddPara("Words",n),i.AddPara("code",e),yield i.DoMethodReturnString("CodeGener_Save")})}GenerCodeJava(){return l(this,null,function*(){const e="java",n=this.RequestVal("tb1","CodeJava"),i=new s("BP.WF.HttpHandler.WF_Admin_AI");return i.AddPara("FrmID",this.RequestVal("FrmID")),i.AddPara("Words",n),i.AddPara("code",e),yield i.DoMethodReturnString("CodeGener_Save")})}GenerCode(){return l(this,null,function*(){let e="javascript",n=this.RequestVal("tb1","CodeTS"),i=n;n!=null&&n!=null&&n!=""&&(e="csharp",i=n),n=this.RequestVal("tb1","CodeCSharp"),n!=null&&n!=null&&n!=""&&(e="csharp",i=n),n=this.RequestVal("tb1","CodeJava"),n!=null&&n!=null&&n!=""&&(e="java",i=n);const r=new s("BP.WF.HttpHandler.WF_Admin_AI");r.AddPara("FrmID",this.RequestVal("FrmID")),r.AddPara("Words",i),r.AddPara("code",e);const g=yield r.DoMethodReturnString("CodeGener_Save");return JSON.stringify(g)})}GenerSorts(){return l(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,n,i,r,g){return l(this,null,function*(){if(e.includes("CodeTSHand")==!0)return new D(f.Message,"已经生成成功了，请把copy到您代码里.");if(e.includes("SelectedAttrs")){const o=new s("BP.WF.HttpHandler.WF_Admin_AI");o.AddPara("FrmID",this.RequestVal("FrmID")),o.AddPara("SelectedAttrs",i);const a=this.RequestVal("tb1","BatchAddAttrs");a!=null&&a.length>0&&o.AddPara("AttrsFromType","FrmAttrBatch");const u=yield o.DoMethodReturnString("AiFrm_SaveAttrs");return S.emit("reloadForm"),new D(f.CloseAndReload,u)}if(e.includes("Dtl.SelectedDtls.Attrs")){const o=new s("BP.WF.HttpHandler.WF_Admin_AI");o.AddPara("FrmID",this.RequestVal("FrmID")),o.AddPara("DtlID",this.RequestVal("tb1","Dtl.SelectedDtls")),o.AddPara("SelectedAttrs",i);const a=yield o.DoMethodReturnString("AiFrm_DtlSaveAttrs");return S.emit("reloadForm"),new D(f.CloseAndReload,a)}if(e.includes("AIAttrs.FX")){const o=new s("BP.WF.HttpHandler.WF_Admin_AI");o.AddPara("FrmID",this.RequestVal("FrmID")),o.AddPara("Vals",i);const a=yield o.DoMethodReturnString("CheckFrm_GenerSave");return S.emit("reloadForm"),new D(f.CloseAndReload,a)}if(e.includes("Test.Save")){const o=new s("BP.WF.HttpHandler.WF_Admin_AI");o.AddPara("FrmID",this.RequestVal("FrmID")),o.AddPara("Vals",i);const a=yield o.DoMethodReturnString("AiFrm_TestDBSave");return S.emit("reloadForm"),new D(f.CloseAndReload,a)}if(e.includes("Icon.Save")){const o=new s("BP.WF.HttpHandler.WF_Admin_AI");o.AddPara("FrmID",this.RequestVal("FrmID")),o.AddPara("Vals",i);const a=yield o.DoMethodReturnString("AiFrm_IconSave");return S.emit("reloadForm"),new D(f.CloseAndReload,a)}if(e=="File"){alert("开发中.");const o=new s("BP.WF.HttpHandler.Admin_AI");o.AddFile(this.UploadFile),o.AddPara("FlowNo",this.RequestVal("FrmID"));let a=yield o.DoMethodReturnString("AiFrm_File");return typeof a=="string"&&a.includes("@")&&(a=a.split("@").join(`
`)),S.emit("reloadForm"),new D(f.Message,a)}})}}export{x as GPN_AIFrm};
