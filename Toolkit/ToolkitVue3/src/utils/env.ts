import { useUserStore } from '@/stores/user'
//vue3地址
const VITE_GLOB_VUE3_URL = import.meta.env.VITE_GLOB_VUE3_URL
//获取Token
const userStore = useUserStore()
const Token = userStore.getToken;
// 处理包含@符号的字符串
export function ccbpmURL(str: string) {
    const url = VITE_GLOB_VUE3_URL + str + '&token=' + Token;
    return url
}
// //调用Vue3的Port页面
export function ccbpmPortURL(str: string) {
    const url = VITE_GLOB_VUE3_URL + '/#/WF/Port?' + str + '&token='+Token +'&win=true';
    console.log(url);
    return url
}
// //调用Vue3的Port页面 //表单跟流程使用这个
export function ccbpmPortURLCall(str: string) {
    const url = VITE_GLOB_VUE3_URL + '/#/WF/Port?' + str + '&token='+Token;
    console.log(url);
    return url
}
