import{userNodes as K,routeNodes as tt,ccNodes as et,subFlowNodes as it,labelNodes as st}from"./x6Shapes-B7W1B6S_.js";import{V as A,e as J,b as d,G as H,d as N,f as S,g as k,h as nt,r as v,j as V,R as ot,k as F,t as at,l as _,M as O,m as L,n as T,o as G,N as X,p as P}from"./html-Bx-Wc9HU.js";import{o as rt}from"./position-D5FSIwnL.js";import{bZ as lt}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";const ct=`.x6-widget-dnd {
  position: absolute;
  top: -10000px;
  left: -10000px;
  z-index: 999999;
  display: none;
  cursor: move;
  opacity: 0.7;
  pointer-events: 'cursor';
}
.x6-widget-dnd.dragging {
  display: inline-block;
}
.x6-widget-dnd.dragging * {
  pointer-events: none !important;
}
.x6-widget-dnd .x6-graph {
  background: transparent;
  box-shadow: none;
}
`;var gt=function(l,t,e,i){var n=arguments.length,s=n<3?t:i===null?i=Object.getOwnPropertyDescriptor(t,e):i,o;if(typeof Reflect=="object"&&typeof Reflect.decorate=="function")s=Reflect.decorate(l,t,e,i);else for(var a=l.length-1;a>=0;a--)(o=l[a])&&(s=(n<3?o(s):n>3?o(t,e,s):o(t,e))||s);return n>3&&s&&Object.defineProperty(t,e,s),s};class w extends A{get targetScroller(){return this.options.target.getPlugin("scroller")}get targetGraph(){return this.options.target}get targetModel(){return this.targetGraph.model}get snapline(){return this.options.target.getPlugin("snapline")}constructor(t){super(),this.name="dnd",this.options=Object.assign(Object.assign({},w.defaults),t),this.init()}init(){J(this.name,ct),this.container=document.createElement("div"),d(this.container,this.prefixClassName("widget-dnd")),this.draggingGraph=new H(Object.assign(Object.assign({},this.options.delegateGraphOptions),{container:document.createElement("div"),width:1,height:1,async:!1})),N(this.container,this.draggingGraph.container)}start(t,e){const i=e;i.preventDefault(),this.targetModel.startBatch("dnd"),d(this.container,"dragging"),S(this.container,this.options.draggingContainer||document.body),this.sourceNode=t,this.prepareDragging(t,i.clientX,i.clientY);const n=this.updateNodePosition(i.clientX,i.clientY);this.isSnaplineEnabled()&&(this.snapline.captureCursorOffset({e:i,node:t,cell:t,view:this.draggingView,x:n.x,y:n.y}),this.draggingNode.on("change:position",this.snap,this)),this.delegateDocumentEvents(w.documentEvents,i.data)}isSnaplineEnabled(){return this.snapline&&this.snapline.isEnabled()}prepareDragging(t,e,i){const n=this.draggingGraph,s=n.model,o=this.options.getDragNode(t,{sourceNode:t,draggingGraph:n,targetGraph:this.targetGraph});o.position(0,0);let a=5;if(this.isSnaplineEnabled()&&(a+=this.snapline.options.tolerance||0),this.isSnaplineEnabled()||this.options.scaled){const g=this.targetGraph.transform.getScale();n.scale(g.sx,g.sy),a*=Math.max(g.sx,g.sy)}else n.scale(1,1);this.clearDragging(),s.resetCells([o]);const r=n.findViewByCell(o);r.undelegateEvents(),r.cell.off("changed"),n.fitToContent({padding:a,allowNewOrigin:"any",useCellGeometry:!1});const c=r.getBBox();this.geometryBBox=r.getBBox({useCellGeometry:!0}),this.delta=this.geometryBBox.getTopLeft().diff(c.getTopLeft()),this.draggingNode=o,this.draggingView=r,this.draggingBBox=o.getBBox(),this.padding=a,this.originOffset=this.updateGraphPosition(e,i)}updateGraphPosition(t,e){const i=document.body.scrollTop||document.documentElement.scrollTop,n=document.body.scrollLeft||document.documentElement.scrollLeft,s=this.delta,o=this.geometryBBox,a=this.padding||5,r={left:t-s.x-o.width/2-a+n,top:e-s.y-o.height/2-a+i};return this.draggingGraph&&k(this.container,{left:`${r.left}px`,top:`${r.top}px`}),r}updateNodePosition(t,e){const i=this.targetGraph.clientToLocal(t,e),n=this.draggingBBox;return i.x-=n.width/2,i.y-=n.height/2,this.draggingNode.position(i.x,i.y),i}snap({cell:t,current:e,options:i}){const n=t;if(i.snapped){const s=this.draggingBBox;n.position(s.x+i.tx,s.y+i.ty,{silent:!0}),this.draggingView.translate(),n.position(e.x,e.y,{silent:!0}),this.snapOffset={x:i.tx,y:i.ty}}else this.snapOffset=null}onDragging(t){const e=this.draggingView;if(e){t.preventDefault();const i=this.normalizeEvent(t),n=i.clientX,s=i.clientY;this.updateGraphPosition(n,s);const o=this.updateNodePosition(n,s),a=this.targetGraph.options.embedding.enabled,r=(a||this.isSnaplineEnabled())&&this.isInsideValidArea({x:n,y:s});if(a){e.setEventData(i,{graph:this.targetGraph,candidateEmbedView:this.candidateEmbedView});const c=e.getEventData(i);r?e.processEmbedding(i,c):e.clearEmbedding(c),this.candidateEmbedView=c.candidateEmbedView}this.isSnaplineEnabled()&&(r?this.snapline.snapOnMoving({e:i,view:e,x:o.x,y:o.y}):this.snapline.hide())}}onDragEnd(t){const e=this.draggingNode;if(e){const i=this.normalizeEvent(t),n=this.draggingView,s=this.draggingBBox,o=this.snapOffset;let a=s.x,r=s.y;o&&(a+=o.x,r+=o.y),e.position(a,r,{silent:!0});const c=this.drop(e,{x:i.clientX,y:i.clientY}),g=u=>{u?(this.onDropped(e),this.targetGraph.options.embedding.enabled&&n&&(n.setEventData(i,{cell:u,graph:this.targetGraph,candidateEmbedView:this.candidateEmbedView}),n.finalizeEmbedding(i,n.getEventData(i)))):this.onDropInvalid(),this.candidateEmbedView=null,this.targetModel.stopBatch("dnd")};nt(c)?(this.undelegateDocumentEvents(),c.then(g)):g(c)}}clearDragging(){this.draggingNode&&(this.sourceNode=null,this.draggingNode.remove(),this.draggingNode=null,this.draggingView=null,this.delta=null,this.padding=null,this.snapOffset=null,this.originOffset=null,this.undelegateDocumentEvents())}onDropped(t){this.draggingNode===t&&(this.clearDragging(),v(this.container,"dragging"),V(this.container))}onDropInvalid(){const t=this.draggingNode;t&&this.onDropped(t)}isInsideValidArea(t){let e,i=null;const n=this.targetGraph,s=this.targetScroller;this.options.dndContainer&&(i=this.getDropArea(this.options.dndContainer));const o=i&&i.containsPoint(t);if(s)if(s.options.autoResize)e=this.getDropArea(s.container);else{const a=this.getDropArea(s.container);e=this.getDropArea(n.container).intersectsWithRect(a)}else e=this.getDropArea(n.container);return!o&&e&&e.containsPoint(t)}getDropArea(t){const e=rt(t),i=document.body.scrollTop||document.documentElement.scrollTop,n=document.body.scrollLeft||document.documentElement.scrollLeft;return ot.create({x:e.left+parseInt(k(t,"border-left-width"),10)-n,y:e.top+parseInt(k(t,"border-top-width"),10)-i,width:t.clientWidth,height:t.clientHeight})}drop(t,e){if(this.isInsideValidArea(e)){const i=this.targetGraph,n=i.model,s=i.clientToLocal(e),o=this.sourceNode,a=this.options.getDropNode(t,{sourceNode:o,draggingNode:t,targetGraph:this.targetGraph,draggingGraph:this.draggingGraph}),r=a.getBBox();s.x+=r.x-r.width/2,s.y+=r.y-r.height/2;const c=this.snapOffset?1:i.getGridSize();a.position(F.snapToGrid(s.x,c),F.snapToGrid(s.y,c)),a.removeZIndex();const g=this.options.validateNode,u=g?g(a,{sourceNode:o,draggingNode:t,droppingNode:a,targetGraph:i,draggingGraph:this.draggingGraph}):!0;return typeof u=="boolean"?u?(n.addCell(a,{stencil:this.cid}),a):null:at(u).then(x=>x?(n.addCell(a,{stencil:this.cid}),a):null)}return null}onRemove(){this.draggingGraph&&(this.draggingGraph.view.remove(),this.draggingGraph.dispose())}dispose(){this.remove(),_(this.name)}}gt([A.dispose()],w.prototype,"dispose",null);(function(l){l.defaults={getDragNode:t=>t.clone(),getDropNode:t=>t.clone()},l.documentEvents={mousemove:"onDragging",touchmove:"onDragging",mouseup:"onDragEnd",touchend:"onDragEnd",touchcancel:"onDragEnd"}})(w||(w={}));function ht(l,t={}){const e=O.isModel(l)?l:new O().resetCells(l,{sort:!1,dryrun:!0}),i=e.getNodes(),n=t.columns||1,s=Math.ceil(i.length/n),o=t.dx||0,a=t.dy||0,r=t.center!==!1,c=t.resizeToFit===!0,g=t.marginX||0,u=t.marginY||0,x=[];let f=t.columnWidth;if(f==="compact")for(let h=0;h<n;h+=1){const y=m.getNodesInColumn(i,h,n);x.push(m.getMaxDim(y,"width")+o)}else{(f==null||f==="auto")&&(f=m.getMaxDim(i,"width")+o);for(let h=0;h<n;h+=1)x.push(f)}const C=m.accumulate(x,g),E=[];let D=t.rowHeight;if(D==="compact")for(let h=0;h<s;h+=1){const y=m.getNodesInRow(i,h,n);E.push(m.getMaxDim(y,"height")+a)}else{(D==null||D==="auto")&&(D=m.getMaxDim(i,"height")+a);for(let h=0;h<s;h+=1)E.push(D)}const Q=m.accumulate(E,u);e.startBatch("layout"),i.forEach((h,y)=>{const W=y%n,U=Math.floor(y/n),Z=x[W],I=E[U];let R=0,$=0,b=h.getSize();if(c){let z=Z-2*o,B=I-2*a;const Y=b.height*(b.width?z/b.width:1),q=b.width*(b.height?B/b.height:1);I<Y?z=q:B=Y,b={width:z,height:B},h.setSize(b,t)}r&&(R=(Z-b.width)/2,$=(I-b.height)/2),h.position(C[W]+o+R,Q[U]+a+$,t)}),e.stopBatch("layout")}var m;(function(l){function t(s,o){return s.reduce((a,r)=>Math.max(r==null?void 0:r.getSize()[o],a),0)}l.getMaxDim=t;function e(s,o,a){const r=[];for(let c=a*o,g=c+a;c<g;c+=1)s[c]&&r.push(s[c]);return r}l.getNodesInRow=e;function i(s,o,a){const r=[];for(let c=o,g=s.length;c<g;c+=a)s[c]&&r.push(s[c]);return r}l.getNodesInColumn=i;function n(s,o){return s.reduce((a,r,c)=>(a.push(a[c]+r),a),[o||0])}l.accumulate=n})(m||(m={}));const dt=`.x6-widget-dnd {
  position: absolute;
  top: -10000px;
  left: -10000px;
  z-index: 999999;
  display: none;
  cursor: move;
  opacity: 0.7;
  pointer-events: 'cursor';
}
.x6-widget-dnd.dragging {
  display: inline-block;
}
.x6-widget-dnd.dragging * {
  pointer-events: none !important;
}
.x6-widget-dnd .x6-graph {
  background: transparent;
  box-shadow: none;
}
.x6-widget-stencil {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}
.x6-widget-stencil::after {
  position: absolute;
  top: 0;
  display: block;
  width: 100%;
  height: 20px;
  padding: 8px 0;
  line-height: 20px;
  text-align: center;
  opacity: 0;
  transition: top 0.1s linear, opacity 0.1s linear;
  content: ' ';
  pointer-events: none;
}
.x6-widget-stencil-content {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  height: auto;
  overflow-x: hidden;
  overflow-y: auto;
}
.x6-widget-stencil .x6-node [magnet]:not([magnet='passive']) {
  pointer-events: none;
}
.x6-widget-stencil-group {
  padding: 0;
  padding-bottom: 8px;
  overflow: hidden;
  user-select: none;
}
.x6-widget-stencil-group.collapsed {
  height: auto;
  padding-bottom: 0;
}
.x6-widget-stencil-group-title {
  position: relative;
  margin-top: 0;
  margin-bottom: 0;
  padding: 4px;
  cursor: pointer;
}
.x6-widget-stencil-title,
.x6-widget-stencil-group > .x6-widget-stencil-group-title {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  user-select: none;
}
.x6-widget-stencil .unmatched {
  opacity: 0.3;
}
.x6-widget-stencil .x6-node.unmatched {
  display: none;
}
.x6-widget-stencil-group.unmatched {
  display: none;
}
.x6-widget-stencil-search-text {
  position: relative;
  z-index: 1;
  box-sizing: border-box;
  width: 100%;
  height: 30px;
  max-height: 30px;
  line-height: 30px;
  outline: 0;
}
.x6-widget-stencil.not-found::after {
  opacity: 1;
  content: attr(data-not-found-text);
}
.x6-widget-stencil.not-found.searchable::after {
  top: 30px;
}
.x6-widget-stencil.not-found.searchable.collapsable::after {
  top: 50px;
}
.x6-widget-stencil {
  color: #333;
  background: #f5f5f5;
}
.x6-widget-stencil-content {
  position: absolute;
}
.x6-widget-stencil.collapsable > .x6-widget-stencil-content {
  top: 32px;
}
.x6-widget-stencil.searchable > .x6-widget-stencil-content {
  top: 80px;
}
.x6-widget-stencil.not-found::after {
  position: absolute;
}
.x6-widget-stencil.not-found.searchable.collapsable::after {
  top: 80px;
}
.x6-widget-stencil.not-found.searchable::after {
  top: 60px;
}
.x6-widget-stencil-group {
  height: auto;
  margin-bottom: 1px;
  padding: 0;
  transition: none;
}
.x6-widget-stencil-group .x6-graph {
  background: transparent;
  box-shadow: none;
}
.x6-widget-stencil-group.collapsed {
  height: auto;
  max-height: 31px;
}
.x6-widget-stencil-title,
.x6-widget-stencil-group > .x6-widget-stencil-group-title {
  position: relative;
  left: 0;
  box-sizing: border-box;
  width: 100%;
  height: 32px;
  padding: 0 5px 0 8px;
  color: #666;
  font-weight: 700;
  font-size: 12px;
  line-height: 32px;
  cursor: default;
  transition: all 0.3;
}
.x6-widget-stencil-title:hover,
.x6-widget-stencil-group > .x6-widget-stencil-group-title:hover {
  color: #444;
}
.x6-widget-stencil-title {
  background: #e9e9e9;
}
.x6-widget-stencil-group > .x6-widget-stencil-group-title {
  background: #ededed;
}
.x6-widget-stencil.collapsable > .x6-widget-stencil-title,
.x6-widget-stencil-group.collapsable > .x6-widget-stencil-group-title {
  padding-left: 32px;
  cursor: pointer;
}
.x6-widget-stencil.collapsable > .x6-widget-stencil-title::before,
.x6-widget-stencil-group.collapsable > .x6-widget-stencil-group-title::before {
  position: absolute;
  top: 6px;
  left: 8px;
  display: block;
  width: 18px;
  height: 18px;
  margin: 0;
  padding: 0;
  background-color: transparent;
  background-repeat: no-repeat;
  background-position: 0 0;
  border: none;
  content: ' ';
}
.x6-widget-stencil.collapsable > .x6-widget-stencil-title::before,
.x6-widget-stencil-group.collapsable > .x6-widget-stencil-group-title::before {
  background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTgiIGhlaWdodD0iMTgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0iIzAwMCIgZmlsbC1ydWxlPSJub256ZXJvIj48cGF0aCBkPSJNOS4zNzUuNUM0LjY4Ny41Ljg3NSA0LjMxMy44NzUgOWMwIDQuNjg4IDMuODEyIDguNSA4LjUgOC41IDQuNjg3IDAgOC41LTMuODEyIDguNS04LjUgMC00LjY4Ny0zLjgxMy04LjUtOC41LTguNXptMCAxNS44ODZDNS4zMDMgMTYuMzg2IDEuOTkgMTMuMDcyIDEuOTkgOXMzLjMxMi03LjM4NSA3LjM4NS03LjM4NVMxNi43NiA0LjkyOCAxNi43NiA5YzAgNC4wNzItMy4zMTMgNy4zODYtNy4zODUgNy4zODZ6Ii8+PHBhdGggZD0iTTEyLjc1MyA4LjQ0M0g1Ljk5N2EuNTU4LjU1OCAwIDAwMCAxLjExNmg2Ljc1NmEuNTU4LjU1OCAwIDAwMC0xLjExNnoiLz48L2c+PC9zdmc+');
  opacity: 0.4;
  transition: all 0.3s;
}
.x6-widget-stencil.collapsable > .x6-widget-stencil-title:hover::before,
.x6-widget-stencil-group.collapsable > .x6-widget-stencil-group-title:hover::before {
  opacity: 0.6;
}
.x6-widget-stencil.collapsable.collapsed > .x6-widget-stencil-title::before,
.x6-widget-stencil-group.collapsable.collapsed > .x6-widget-stencil-group-title::before {
  background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTgiIGhlaWdodD0iMTgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0iIzAwMCIgZmlsbC1ydWxlPSJub256ZXJvIj48cGF0aCBkPSJNOS4zNzUuNUM0LjY4Ny41Ljg3NSA0LjMxMy44NzUgOWMwIDQuNjg4IDMuODEyIDguNSA4LjUgOC41IDQuNjg3IDAgOC41LTMuODEyIDguNS04LjUgMC00LjY4Ny0zLjgxMy04LjUtOC41LTguNXptMCAxNS44ODZDNS4zMDMgMTYuMzg2IDEuOTkgMTMuMDcyIDEuOTkgOXMzLjMxMi03LjM4NSA3LjM4NS03LjM4NVMxNi43NiA0LjkyOCAxNi43NiA5YzAgNC4wNzItMy4zMTMgNy4zODYtNy4zODUgNy4zODZ6Ii8+PHBhdGggZD0iTTEyLjc1MyA4LjQ0M0g1Ljk5N2EuNTU4LjU1OCAwIDAwMCAxLjExNmg2Ljc1NmEuNTU4LjU1OCAwIDAwMC0xLjExNnoiLz48cGF0aCBkPSJNOC44MTcgNS42MjN2Ni43NTZhLjU1OC41NTggMCAwMDEuMTE2IDBWNS42MjNhLjU1OC41NTggMCAxMC0xLjExNiAweiIvPjwvZz48L3N2Zz4=');
  opacity: 0.4;
}
.x6-widget-stencil.collapsable.collapsed > .x6-widget-stencil-title:hover::before,
.x6-widget-stencil-group.collapsable.collapsed > .x6-widget-stencil-group-title:hover::before {
  opacity: 0.6;
}
.x6-widget-stencil input[type='search'] {
  appearance: textfield;
}
.x6-widget-stencil input[type='search']::-webkit-search-cancel-button,
.x6-widget-stencil input[type='search']::-webkit-search-decoration {
  appearance: none;
}
.x6-widget-stencil-search-text {
  display: block;
  width: 90%;
  margin: 8px 5%;
  padding-left: 8px;
  color: #333;
  background: #fff;
  border: 1px solid #e9e9e9;
  border-radius: 12px;
  outline: 0;
}
.x6-widget-stencil-search-text:focus {
  outline: 0;
}
.x6-widget-stencil::after {
  color: #808080;
  font-weight: 600;
  font-size: 12px;
  background: 0 0;
}
`;var pt=function(l,t,e,i){var n=arguments.length,s=n<3?t:i===null?i=Object.getOwnPropertyDescriptor(t,e):i,o;if(typeof Reflect=="object"&&typeof Reflect.decorate=="function")s=Reflect.decorate(l,t,e,i);else for(var a=l.length-1;a>=0;a--)(o=l[a])&&(s=(n<3?o(s):n>3?o(t,e,s):o(t,e))||s);return n>3&&s&&Object.defineProperty(t,e,s),s};class M extends A{get targetScroller(){return this.options.target.getPlugin("scroller")}get targetGraph(){return this.options.target}get targetModel(){return this.targetGraph.model}constructor(t={}){super(),this.name="stencil",J(this.name,dt),this.graphs={},this.groups={},this.options=Object.assign(Object.assign({},M.defaultOptions),t),this.init()}init(){this.dnd=new w(this.options),this.onSearch=lt(this.onSearch,200),this.initContainer(),this.initSearch(),this.initContent(),this.initGroups(),this.setTitle(),this.startListening()}load(t,e){return Array.isArray(t)?this.loadGroup(t,e):this.options.groups&&Object.keys(this.options.groups).forEach(i=>{t[i]&&this.loadGroup(t[i],i)}),this}unload(t,e){return Array.isArray(t)?this.loadGroup(t,e,!0):this.options.groups&&Object.keys(this.options.groups).forEach(i=>{t[i]&&this.loadGroup(t[i],i,!0)}),this}toggleGroup(t){return this.isGroupCollapsed(t)?this.expandGroup(t):this.collapseGroup(t),this}collapseGroup(t){if(this.isGroupCollapsable(t)){const e=this.groups[t];e&&!this.isGroupCollapsed(t)&&(this.trigger("group:collapse",{name:t}),d(e,"collapsed"))}return this}expandGroup(t){if(this.isGroupCollapsable(t)){const e=this.groups[t];e&&this.isGroupCollapsed(t)&&(this.trigger("group:expand",{name:t}),v(e,"collapsed"))}return this}isGroupCollapsable(t){const e=this.groups[t];return L(e,"collapsable")}isGroupCollapsed(t){const e=this.groups[t];return e&&L(e,"collapsed")}collapseGroups(){return Object.keys(this.groups).forEach(t=>this.collapseGroup(t)),this}expandGroups(){return Object.keys(this.groups).forEach(t=>this.expandGroup(t)),this}resizeGroup(t,e){const i=this.graphs[t];return i&&i.resize(e.width,e.height),this}addGroup(t){const e=Array.isArray(t)?t:[t];this.options.groups?this.options.groups.push(...e):this.options.groups=e,e.forEach(i=>this.initGroup(i))}removeGroup(t){const e=Array.isArray(t)?t:[t];this.options.groups&&(this.options.groups=this.options.groups.filter(i=>!e.includes(i.name)),e.forEach(i=>{this.graphs[i].dispose(),delete this.graphs[i];const s=this.groups[i];V(s),delete this.groups[i]}))}initContainer(){this.container=document.createElement("div"),d(this.container,this.prefixClassName(p.base)),T(this.container,"data-not-found-text",this.options.notFoundText||"No matches found")}initContent(){this.content=document.createElement("div"),d(this.content,this.prefixClassName(p.content)),S(this.content,this.container)}initSearch(){this.options.search&&(d(this.container,"searchable"),N(this.container,this.renderSearch()))}initGroup(t){const e=this.options.stencilGraphOptions||{},i=document.createElement("div");d(i,this.prefixClassName(p.group)),T(i,"data-name",t.name),(t.collapsable==null&&this.options.collapsable||t.collapsable!==!1)&&d(i,"collapsable"),G(i,"collapsed",t.collapsed===!0);const n=document.createElement("h3");d(n,this.prefixClassName(p.groupTitle)),n.innerHTML=t.title||t.name;const s=document.createElement("div");d(s,this.prefixClassName(p.groupContent));const o=t.graphOptions,a=new H(Object.assign(Object.assign(Object.assign({},e),o),{container:document.createElement("div"),model:e.model||new O,width:t.graphWidth||this.options.stencilGraphWidth,height:t.graphHeight||this.options.stencilGraphHeight,interacting:!1,preventDefaultBlankAction:!1}));N(s,a.container),N(i,[n,s]),S(i,this.content),this.groups[t.name]=i,this.graphs[t.name]=a}initGroups(){if(this.clearGroups(),this.setCollapsableState(),this.options.groups&&this.options.groups.length)this.options.groups.forEach(t=>{this.initGroup(t)});else{const t=this.options.stencilGraphOptions||{},e=new H(Object.assign(Object.assign({},t),{container:document.createElement("div"),model:t.model||new O,width:this.options.stencilGraphWidth,height:this.options.stencilGraphHeight,interacting:!1,preventDefaultBlankAction:!1}));N(this.content,e.container),this.graphs[j.defaultGroupName]=e}}setCollapsableState(){this.options.collapsable=this.options.collapsable&&this.options.groups&&this.options.groups.some(t=>t.collapsable!==!1),this.options.collapsable?(d(this.container,"collapsable"),this.options.groups&&this.options.groups.every(e=>e.collapsed||e.collapsable===!1)?d(this.container,"collapsed"):v(this.container,"collapsed")):v(this.container,"collapsable")}setTitle(){const t=document.createElement("div");d(t,this.prefixClassName(p.title)),t.innerHTML=this.options.title,S(t,this.container)}renderSearch(){const t=document.createElement("div");d(t,this.prefixClassName(p.search));const e=document.createElement("input");return T(e,{type:"search",placeholder:this.options.placeholder||"Search"}),d(e,this.prefixClassName(p.searchText)),N(t,e),t}startListening(){const t=this.prefixClassName(p.title),e=this.prefixClassName(p.searchText),i=this.prefixClassName(p.groupTitle);this.delegateEvents({[`click .${t}`]:"onTitleClick",[`touchstart .${t}`]:"onTitleClick",[`click .${i}`]:"onGroupTitleClick",[`touchstart .${i}`]:"onGroupTitleClick",[`input .${e}`]:"onSearch",[`focusin .${e}`]:"onSearchFocusIn",[`focusout .${e}`]:"onSearchFocusOut"}),Object.keys(this.graphs).forEach(n=>{this.graphs[n].on("cell:mousedown",this.onDragStart,this)})}stopListening(){this.undelegateEvents(),Object.keys(this.graphs).forEach(t=>{this.graphs[t].off("cell:mousedown",this.onDragStart,this)})}loadGroup(t,e,i){const n=this.getModel(e);if(n){const r=t.map(c=>X.isNode(c)?c:X.create(c));i===!0?n.removeCells(r):n.resetCells(r)}const s=this.getGroup(e);let o=this.options.stencilGraphHeight;s&&s.graphHeight!=null&&(o=s.graphHeight);const a=s&&s.layout||this.options.layout;if(a&&n&&P(a,this,n,s),!o){const r=this.getGraph(e);r.fitToContent({minWidth:r.options.width,gridHeight:1,padding:s&&s.graphPadding||this.options.stencilGraphPadding||10})}return this}onDragStart(t){const{e,node:i}=t,n=this.getGroupByNode(i);n&&n.nodeMovable===!1||this.dnd.start(i,e)}filter(t,e){const i=Object.keys(this.graphs).reduce((n,s)=>{const o=this.graphs[s],a=s===j.defaultGroupName?null:s,r=o.model.getNodes().filter(x=>{let f=!1;typeof e=="function"?f=P(e,this,x,t,a,this):typeof e=="boolean"?f=e:f=this.isCellMatched(x,t,e,t.toLowerCase()!==t);const C=o.renderer.findViewByCell(x);return C&&G(C.container,"unmatched",!f),f}),c=r.length>0,g=this.options,u=new O;return u.resetCells(r),g.layout&&P(g.layout,this,u,this.getGroup(s)),this.groups[s]&&G(this.groups[s],"unmatched",!c),o.fitToContent({gridWidth:1,gridHeight:1,padding:g.stencilGraphPadding||10}),n||c},!1);G(this.container,"not-found",!i)}isCellMatched(t,e,i,n){return e&&i?Object.keys(i).some(s=>{if(s==="*"||t.shape===s){const o=i[s];return typeof o=="boolean"?o:(Array.isArray(o)?o:[o]).some(r=>{let c=t.getPropByPath(r);return c!=null?(c=`${c}`,n||(c=c.toLowerCase()),c.indexOf(e)>=0):!1})}return!1}):!0}onSearch(t){this.filter(t.target.value,this.options.search)}onSearchFocusIn(){d(this.container,"is-focused")}onSearchFocusOut(){v(this.container,"is-focused")}onTitleClick(){this.options.collapsable&&(G(this.container,"collapsed"),L(this.container,"collapsed")?this.collapseGroups():this.expandGroups())}onGroupTitleClick(t){const e=t.target.closest(`.${this.prefixClassName(p.group)}`);e&&this.toggleGroup(T(e,"data-name")||"");const i=Object.keys(this.groups).every(n=>{const s=this.getGroup(n),o=this.groups[n];return s&&s.collapsable===!1||L(o,"collapsed")});G(this.container,"collapsed",i)}getModel(t){const e=this.getGraph(t);return e?e.model:null}getGraph(t){return this.graphs[t||j.defaultGroupName]}getGroup(t){const e=this.options.groups;return t!=null&&e&&e.length?e.find(i=>i.name===t):null}getGroupByNode(t){const e=this.options.groups;return e?e.find(i=>{const n=this.getModel(i.name);return n?n.has(t.id):!1}):null}clearGroups(){Object.keys(this.graphs).forEach(t=>{this.graphs[t].dispose()}),Object.keys(this.groups).forEach(t=>{const e=this.groups[t];V(e)}),this.graphs={},this.groups={}}onRemove(){this.clearGroups(),this.dnd.remove(),this.stopListening(),this.undelegateDocumentEvents()}dispose(){this.remove(),_(this.name)}}pt([A.dispose()],M.prototype,"dispose",null);(function(l){l.defaultOptions=Object.assign({stencilGraphWidth:200,stencilGraphHeight:800,title:"Stencil",collapsable:!1,placeholder:"Search",notFoundText:"No matches found",layout(t,e){const i={columnWidth:this.options.stencilGraphWidth/2-10,columns:2,rowHeight:80,resizeToFit:!1,dx:10,dy:10};ht(t,Object.assign(Object.assign(Object.assign({},i),this.options.layoutOptions),e?e.layoutOptions:{}))}},w.defaults)})(M||(M={}));var p;(function(l){l.base="widget-stencil",l.title=`${l.base}-title`,l.search=`${l.base}-search`,l.searchText=`${l.search}-text`,l.content=`${l.base}-content`,l.group=`${l.base}-group`,l.groupTitle=`${l.group}-title`,l.groupContent=`${l.group}-content`})(p||(p={}));var j;(function(l){l.defaultGroupName="__default__"})(j||(j={}));function ut(l,t,e,i){const n=[];for(const s of e){const o=l.createNode(s);n.push(o)}t.load(n,i)}function yt(l,t){const e=[{title:"用户节点",name:"userNode",list:K},{title:"路由节点",name:"routeNode",list:tt},{title:"抄送节点",name:"ccNode",list:et},{title:"子流程节点",name:"subFlowNode",list:it},{title:"标签",name:"labelNode",list:st}],i=new M({title:"流程设计器",target:l,stencilGraphWidth:180,stencilGraphHeight:0,collapsable:!1,groups:e.map(({title:n,name:s})=>({title:n,name:s,collapsable:!1})),layoutOptions:{columns:1,columnWidth:180,rowHeight:55}});t==null||t.appendChild(i.container);for(const n of e)ut(l,i,n.list,n.name)}export{yt as initStencil};
