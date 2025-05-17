function i(r){const n=r.lastIndexOf("err@");if(n===-1)return r;const t=r.substring(n+4),e=t.indexOf("@");return e===-1?t.trim():t.substring(0,e).trim()}export{i as errorMsg};
