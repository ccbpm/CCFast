var E=Object.defineProperty;var H=(e,i,a)=>i in e?E(e,i,{enumerable:!0,configurable:!0,writable:!0,value:a}):e[i]=a;var s=(e,i,a)=>H(e,typeof i!="symbol"?i+"":i,a);var r=(e,i,a)=>new Promise((S,h)=>{var Z=n=>{try{o(a.next(n))}catch(t){h(t)}},c=n=>{try{o(a.throw(n))}catch(t){h(t)}},o=n=>n.done?S(n.value):Promise.resolve(n.value).then(Z,c);o((a=a.apply(e,i)).next())});import{b9 as w,aB as F,aC as K}from"./entry/index-M8VErHPE-1727507756861.js";import Y from"./Dev2Interface-BNvJe3G3.js";import{Prj as y}from"./Prj-BF05bGeW.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class f extends w{constructor(){super("GPN_StartFlow009");s(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);s(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 . @hongyan 完善测试该方法.

  `);this.PageTitle="非轨道收入子项确认",this.ForEntityClassID="TS.PM.FGDSubproject"}Init(){return r(this,null,function*(){this.AddGroup("A","选择方式"),this.Table("Track0","选择项目",this.HelpTodo,!1,"SELECT No,No 项目编号,PrjName Name,ZXMBHT 主项目名称,LiXiangShiJian 立项时间,JSDWMCT 建设单位,DJGLDWT  代建管理单位名称,ZCBDWT 总承包单位名称,SJDWMCT 设计单位,KCSWMCT 勘察单位 FROM PM_Prj ")})}GenerSorts(){return r(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(a,S,h,Z,c){return r(this,null,function*(){if(a=="Track0"){const o=yield Y.Node_CreateBlank("009"),n=new y;n.setPKVal(h),yield n.Retrieve();const t=n.No,C=n.PrjName,u=n.SFZXM,J=n.ZXMBH,g=n.LiXiangShiJian,M=n.JSDWMCT,T=n.JSDW_Linker,X=n.JSDW_Tel,D=n.GongChengGaiKuang,G=n.XMLX,L=n.SQL_ZhuanYe,l=n.SQL_ZhuanYeT,B=n.GZLJSYJ,p=n.SheJiHeTongJinE,W=n.SheJiZhanBi,_=n.KanChaZhanBi,m=n.SJWCZCDZGCTZEZMJQT,N=n.KanChaWanChengZongCh,P="/#/WF/MyFlow?FlowNo=009&WorkID="+o+"&XiangMuBianHao="+t+"&XiangMuMingChen="+C+"&ZhuXiangMuBianHao="+J+"&LiXiangShiJian="+g+"&JianSheShanWeiMingCh="+M+"&ShanWeiLianXiRen="+T+"&LianXiRenDianHua="+X+"&GongChengGaiKuang="+D+"&XMLX="+G+"&SQL_ZhuanYe="+L+"&JiSuanYiJu="+B+"&HeTongJinE="+p+"&SheJiZhanBi="+W+"&KanChaZhanBi="+_+"&SJZCDZGCTZEZMJ="+m+"&KCZCDZGCTZEZMJ="+N+"&SFZXM="+u+"&SQL_ZhuanYeT="+l;return new F(K.OpenUrlByDrawer90,P,"流程")}})}}export{f as GPN_StartFlow009};
