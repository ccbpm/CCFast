var N=Object.defineProperty;var h=(a,t,e)=>t in a?N(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var d=(a,t,e)=>h(a,typeof t!="symbol"?t+"":t,e);var s=(a,t,e)=>new Promise((n,i)=>{var y=r=>{try{o(e.next(r))}catch(l){i(l)}},c=r=>{try{o(e.throw(r))}catch(l){i(l)}},o=r=>r.done?n(r.value):Promise.resolve(r.value).then(y,c);o((e=e.apply(a,t)).next())});import{b9 as p,aB as u,aC as w}from"./entry/index-M8VErHPE-1727507756861.js";import A from"./HttpHandler-Ebi1068_.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class D extends p{constructor(){super("GPN_AIFlowCheck");d(this,"DeliveryWay1",`
    #### 说明
    - 接收人规则是指，谁可以处理指定节点的工作.
    - ccbpm提供了30多种接收人规则,是应用不同的场景,比如:绑定接收人，绑定部门，绑定岗位等.
    - AI可以帮助我们把每个节点的接收人规则给与最大的匹配与建议.
    `);d(this,"DeliveryWay2",`
    #### 说明
    - 接收人规则是指，谁可以处理指定节点的工作.
    - ccbpm提供了30多种接收人规则,是应用不同的场景,比如:绑定接收人，绑定部门，绑定岗位等.
    - AI可以帮助我们把每个节点的接收人规则给与最大的匹配与建议.
    `);this.PageTitle="AI流程检查"}Init(){return s(this,null,function*(){this.AddGroup("A","AI流程检查"),this.AddBlank("DeliveryWay","接受人规则",this.DeliveryWay1),this.Table("DeliveryWay.Nodes","内容输出",this.DeliveryWay2,!0,this.GenerNodes)})}GenerNodes(){return s(this,null,function*(){const e=new A("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FlowNo",this.RequestVal("FlowNo"));const n=yield e.DoMethodReturnJson("AiFlow_NodesDeliveryWayGener");return JSON.stringify(n)})}GenerSorts(){return s(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,n,i,y,c){return s(this,null,function*(){if(e=="DeliveryWay.Nodes"){const o=new A("BP.WF.HttpHandler.WF_Admin_AI");o.AddPara("FlowNo",this.RequestVal("FlowNo"));const r=yield o.DoMethodReturnString("AiFlow_NodesDeliveryWaySave");return new u(w.Message,r)}})}}export{D as GPN_AIFlowCheck};
