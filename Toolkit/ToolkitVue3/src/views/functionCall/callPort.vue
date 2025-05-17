<template>
    <iframe v-if="url" :src="url" width="100%" height="100%" style="border: 0"></iframe>
</template>
<script lang="ts" setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { ccbpmPortURLCall } from '@/utils/env';

const route = useRoute();
const url = ref('');
const InitPage = () => {
  url.value =''
  setTimeout(() => {
    if(route.path.includes('FlowTree')){
      url.value = ccbpmPortURLCall('DoWhat=Flows')
    }else if(route.path.includes('FormTree')){
      url.value = ccbpmPortURLCall('DoWhat=Frms')
    }
}, 500);
  
}
InitPage();
// 简单数据类型监听
watch(route, () => {
  InitPage();
}, {
  immediate: true//立即监听--进入就会执行一次
})
</script>