var Z=Object.defineProperty;var $=(u,l,r)=>l in u?Z(u,l,{enumerable:!0,configurable:!0,writable:!0,value:r}):u[l]=r;var K=(u,l,r)=>$(u,typeof l!="symbol"?l+"":l,r);var F=(u,l,r)=>new Promise((S,d)=>{var b=f=>{try{w(r.next(f))}catch(h){d(h)}},R=f=>{try{w(r.throw(f))}catch(h){d(h)}},w=f=>f.done?S(f.value):Promise.resolve(f.value).then(b,R);w((r=r.apply(u,l)).next())});import{b5 as ee,m as te,aG as H,H as j,s as se,G as I,l as x}from"./entry/index-C6uBgOW5-1730430676707.js";import{SysEnums as ne}from"./SysEnum-DlgPT0C2.js";import{D as q}from"./DBAccess-sLO0RM-h.js";import{X as C}from"./xlsx-DGObgwyb.js";import{b as p}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";class Ee extends ee{constructor(){super("GPN_ImpTSEntity");K(this,"Help0",`
  #### 清空方式
  - 首先删除现在的数据，把指定的模板的数据导入到数据库.
  - 执行之前需要慎重.
  `);K(this,"Help1",`
  #### 更新方式导入
  - 在现有的数据基础上追加数据, 如果有主键系统将会更新. 没有主键系统将会自动生成.
  `);K(this,"Help2",`
  #### 追加方式导入
  - 在现有的数据基础上追加数据, 如果有主键系统将会更新. 没有主键系统将会自动生成.
  `);K(this,"HelpIt",`
  #### 数据制作说明
   - 新建一个Excel文件，比如 AAA.xlsx,
   - 在excel的工具栏中，找到文件另存为命令，选择 (Excel 工作簿(*.xlsx))格式. 
   - 在第一行数据填入如下列 </li>
   - 测试该模版是否可用,如果可用就把该文件放到 DatUser\\TempleteOfImp\\ 
   - 如果不可用：请尝试下载一个AccessDatabaseEngine.exe 文件安装到服务器上试试.
   #### 外键字段列
   - 外键字段列，存储的是外键名称， 比如：班级字段，存储的是 '1年级' 
   - 系统导入进去的则是 001 .
   #### 枚举字段
   - 枚举字段，存储的是标签列  比如：性别字段，存储的是 '女' 
   - 系统导入进去的则是 0.
   #### 外部数据源字段
   - 需要两个列, abc 与 abcT
   - abc 存储的是编号, abcT则是中文名称.
   #### Pop字段字段列
   - 列是Pop模式的列,  需要两个字段abc, 与abcT  abc存储 编号列， abcT存储的名称列.
   - 比如：选修科目字段, 要增加一个影子字段 选修科目T, 
   - '选修科目' 是存储的外键数据, 比如:001,002  
   - '选修科目T' 是存储的外键数据, 比如:语文,数学
  `);this.PageTitle="导入实体数据"}Init(){return F(this,null,function*(){this.AddGroup("A","导入数据"),this.FileUpload("0","清空方式导入","请上传符合格式的模板数据,执行之前阅读帮助.",this.Help0),this.FileUpload("1","更新追加方式导入","请上传符合格式的模板数据,执行之前阅读帮助.",this.Help1);const r=this.RequestVal("TSEnName"),{VITE_GLOB_API_URL:S}=te(),d=yield H.GetEn(r);yield d.Init(),d.setPKVal("xx"),yield d.RetrieveFromDBSources();const b=S+"/DataUser/TempleteOfImp/组织结构批量导入模板_集团.xlsx";this.AddGroup("B","模板"),this.AddGoToUrl("DownTemplate","下载模板",b),this.AddHelp("Help","模板说明",this.HelpIt)})}GenerSorts(){return F(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(r,S,d,b,R){return F(this,null,function*(){var w,f,h;if(["0","1","2"].includes(r)){try{const A=this.RequestVal("TSEnName"),V=this.RequestVal("impFuncUrl"),B=yield H.GetEns(A);if(r==="0"){if(!window.confirm("此模式会清空所有数据并进行导入，是否继续?"))return;try{const e=new j("BP.WF.HttpHandler.GPMPage");e.AddFile(this.UploadFile);const t=yield e.DoMethodReturnString("Template_SaveGroupIncByClear");p.success(t)}catch(e){p.error(e)}return}if(r==="1"){if(V!="")try{const e=new FormData;e.append("file",this.UploadFile);const t=yield se.post(V,e),{code:n,msg:o,detailinfo:s}=t;n==200&&p.info(o),p.error(o)}catch(e){p.error(e)}else try{const e=new j("BP.WF.HttpHandler.GPMPage");e.AddFile(this.UploadFile);const t=yield e.DoMethodReturnString("Template_SaveGroupIncByAppend");p.success(t)}catch(e){p.error(e)}return}const _=yield(e=>new Promise((t,n)=>{const o=new FileReader;o.onload=s=>{var m;const y=(m=s.target)==null?void 0:m.result;if(!y){n("文件读取失败");return}const i=C.read(y,{type:"binary"}),a=i.Sheets[i.SheetNames[0]],c=C.utils.sheet_to_json(a,{header:1});t(c)},o.onerror=s=>{n(s)},o.readAsArrayBuffer(e)}))(this.UploadFile),g=_[0],X=_.slice(1).map(e=>{const t={};return g.forEach((n,o)=>{t[n]=e[o]}),t}),T=[],N=B.GetNewEntity,P=N._enMap.attrs,k=N._enMap.enMapExts,Q=P.filter(e=>e.IsFK),D=new Map;for(const e of Q){const t=e.HisFKEns;if(!t)continue;const n=yield H.GetEns(t);yield n.RetrieveAll(),D.set(t,n.map(o=>Object.fromEntries(o.Row)))}const W=P.filter(e=>e.IsDBSource),L=new Map;for(const e of W){const t=e.UIBindKey;t&&L.set(e.Key,yield q.RunSQLReturnTable(t))}const z=P.filter(e=>e.IsEnum),G=new Map;for(const e of z){const{UITag:t,UIBindKey:n}=e;if(!t){const s=new ne;yield s.Retrieve("EnumKey",n),G.set(e.Key,s.map(y=>[y.EnumKey,y.Lab]));continue}if(!(t!=null&&t.includes("@")))continue;const o=t.split("@").filter(s=>!!s).map(s=>s.split("="));G.set(e.Key,o)}const J=k.filter(e=>e.ExtType==="PopList"),M=new Map;for(const e of J){const t=yield q.RunSQLReturnTable(e.Tag2);M.set(e.AttrOfOper,t)}const U={是:1,否:0,true:1,false:0},v=e=>Object.keys(U).includes(e)?U[e]:e;for(const e of X){const t=B.GetNewEntity;for(const n of g){const o=P.find(i=>i.Desc===n);if(!o)continue;const{Key:s,HisFKEns:y}=o;if(o.IsFK){const i=D.get(y);if(!i)continue;const a=i.find(c=>c.Name===e[n]);if(!a)continue;t.SetValByKey(s,a.No);continue}if(o.IsDBSource){const i=L.get(o.Key);if(!i)continue;const a=i.find(c=>c.Name===e[n]);if(!a)continue;t.SetValByKey(s,a.No),t.SetValByKey(s+"T",e[n]);continue}if(o.IsEnum){const a=(w=G.get(s).find(c=>(c==null?void 0:c[1])==e[n]))==null?void 0:w[0];if(!a)continue;t.SetValByKey(s,v(a)),t.SetValByKey(s+"Text",e[n]);continue}if(o.IsBoolean){t.SetValByKey(s,v(e[n]));continue}if(M.has(s)){const i=M.get(s);let a=e[n],c="";if(!a)continue;if(a.includes(",")){a=a.split(",").filter(E=>!!E);const m=[];for(const E of a){const O=(f=i.find(Y=>Y.Name===E))==null?void 0:f.No;O&&m.push(O)}c=m.join(",")}else{const m=(h=i.find(E=>E.Name===a))==null?void 0:h.No;m&&(c=m)}t.SetValByKey(s,c),t.SetValByKey(s+"T",e[n]);continue}t.SetValByKey(s,e[n])}T.push(t)}if(r==="0"){yield B.RetrieveAll();const e=[];for(const t of B)e.push(t.Delete());return p.info("正在清空数据..."),yield Promise.all(e),p.success("清空成功, 执行导入中..."),yield Promise.all(T.map(t=>t.Insert())),new I(x.Message,"清空导入成功")}if(r==="1")return yield Promise.all(T.map(e=>e.Save())),new I(x.Message,"更新导入成功");if(r==="2"){for(const e of T)e.setPKVal(null),yield e.Insert();return new I(x.Message,"追加导入成功")}}catch(A){p.error(A.toString()||"导入失败")}return}return Promise.resolve(new I(x.Message,"未知操作"))})}}export{Ee as GPN_ImpTSEntity};
