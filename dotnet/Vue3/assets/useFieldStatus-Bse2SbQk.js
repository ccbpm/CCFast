function l(n,s,d){return{isHidden:e=>e.Key===n&&!s||!e.UIVisible,isReadOnly:e=>e.UIIsReadonly||e.Key===n&&!!s||e.Key===n&&d}}export{l as useFieldStatus};
