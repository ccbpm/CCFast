<template>
    <div>
        <div class="content">
            <!-- 左侧流程demo -->
            <div class='flow_demo'>
                <!-- 操作步骤 -->
                <Tabs v-model:activeKey="activeKey_demo" type="card" style="height: 60vh;">
                    <TabPane key="1" tab="实例说明">
                        <div class="controls">
                            <div style="font-weight:600">示例1：通过流程编号，创建一个流程操作,并对该流程进行操作</div>
                            <ul>
                                <li>1、创建一个WorkID(实例ID);</li>
                                <li>2、保存草稿，标识该流程实例ID已被占用;</li>
                                <li>3、设置主表数据;</li>
                                <li>4、设置流程变量，这些变量用于控制方向条件，接受人;</li>
                                <li>5、发送到指定的节点指定的人;</li>
                                <li>6、获得流程当前信息,查看当前的节点;</li>
                                <li>7、执行撤销;</li>
                                <li>8、再次执行发送;</li>
                                <li>9、退回操作,退回开始节点;</li>
                                <li>10、执行移交;</li>
                                <li>11、查看流程轨迹 -调用功能页面API;</li>
                                <li>12、执行删除.</li>
                            </ul>
                            <Button  type="primary"  @click="FlowAPI_Demo" class="dir_btn" :loading="demo_disabled">测试</Button>
                        </div>
                    </TabPane>
                    <TabPane key="2" tab="代码说明">
                        <!-- 展示代码模块 -->
                        <div class="modules">
                            <ul>
                                <li>创建一个WorkID(实例ID): <span class="row">Dev2ApiInterface.Node_CreateBlank</span></li>
                                <li>保存草稿，标识该流程实例ID已被占用: <span class="row">Dev2ApiInterface.Node_SetDraft</span></li>
                                <li>设置主表数据: <span class="row">Dev2ApiInterface.Node_SaveWork</span></li>
                                <li>设置流程变量，用于控制方向条件，接受人: <span class="row">Dev2ApiInterface.Flow_SaveParas</span></li>
                                <li>发送到指定的节点,指定的人: <span class="row">Dev2ApiInterface.Node_SendWork</span></li>
                                <li>获得流程当前信息,查看当前的节点: <span class="row">Dev2ApiInterface.Flow_GenerWorkFlow</span></li>
                                <li>执行撤销: <span class="row">Dev2ApiInterface.Flow_DoUnSend</span></li>
                                <li>执行退回: <span class="row">Dev2ApiInterface.Node_ReturnWork</span></li>
                                <li>执行移交: <span class="row">Dev2ApiInterface.Node_ShiftWork</span></li>
                                <li>获取轨迹链接,查看轨迹: <span class="row">Dev2UrlInterface.Flow_MyView</span></li>
                                <li>执行删除: <span class="row">Dev2ApiInterface.Flow_DeleteFlow</span></li>
                            </ul>
                        </div>
                    </TabPane>
                </Tabs>
                <div class="controls">
                    <div v-html="apiDemoMsg"></div>
                </div>
            </div>
            <div class="flow_turn">
                <Tabs v-model:activeKey="activeKey_turn" type="card" style="height: 60vh;">
                    <TabPane key="1" tab="实例说明">
                        <div class="controls">
                            <div style="font-weight:600">示例2：用参数来控制节点的运转</div>
                            <ul class="turn_doc">
                                <li>1、创建一个WorkID(实例ID);</li>
                                <li>2、流程发送到下一个节点;</li>
                                <li>3、获取参数(JE,是否大于1000);</li>
                                <li>4、设置流程变量，这些变量用于控制方向条件，接受人;</li>
                                <li>5、流程发送到下一个节点;</li>
                                <li>6、执行删除.;</li>
                            </ul>
                            <Button  type="primary"  @click="FlowAPI_TurnTo_Demo" class="dir_btn" :loading="turn_disabled" >测试</Button>
                        </div>
                    </TabPane>
                    <TabPane key="2" tab="代码说明">
                        <!-- 展示代码模块 -->
                        <div class="modules">
                            <ul>
                                <li>创建一个WorkID(实例ID): <span class="row">Dev2ApiInterface.Node_CreateBlank</span></li>
                                <li>流程发送到下一个节点: <span class="row">Dev2ApiInterface.Node_SendWork</span></li>
                                <li>设置流程变量，用于控制方向条件，接受人: <span class="row">Dev2ApiInterface.Flow_SaveParas</span></li>
                                <li>执行删除: <span class="row">Dev2ApiInterface.Flow_DeleteFlow</span></li>
                            </ul>
                        </div>
                    </TabPane>
                </Tabs>
                <div class="controls">
                    <div v-html="turnToMsg"></div>
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { TestCase } from '@/Toolkit/TestCase'
import { Button, Tabs, TabPane  } from 'ant-design-vue'
import { onMounted, onUnmounted, ref } from 'vue';
import  Events  from '@/utils/Events';

//tab页签参数
const activeKey_demo = ref('1');
const activeKey_turn = ref('1');


const demo_disabled = ref(false);
const turn_disabled = ref(false);

const turnToMsg = ref();
const apiDemoMsg = ref();
//流程API的demo操作.
const FlowAPI_Demo = async () => {
    demo_disabled.value = true;
    await TestCase.FlowAPI_Demo()
    demo_disabled.value = false
};
//用参数来控制节点的运转.
const FlowAPI_TurnTo_Demo= async ()=>{
    turnToMsg.value = '';
    turn_disabled.value = true;
    await TestCase.FlowAPI_TurnTo_Demo();
    turn_disabled.value = false;
}
/**
 * 事件EventBus处理
 */
const turntoMsgFun = ()=>{
    turnToMsg.value = localStorage.getItem('turnToMsg')
    console.log( turnToMsg.value);
}
const apiDemoMsgFun = ()=>{
    apiDemoMsg.value = localStorage.getItem('apiDemoMsg')
}
onMounted(() => {
    Events.on('turn-to-demo',()=>{
        turntoMsgFun();
    });
    Events.on('api-demo',()=>{
        apiDemoMsgFun();
    })
});
onUnmounted(() => {
    Events.off('turn-to-demo');
    Events.off('api-demo');
  });

</script>
<style lang="less" scoped>
    .content{
        display: flex;
        .flow_demo{
            width: 50%;
            height: 100%;
        }
        .flow_turn{
            width: 50%;
        }
    }
    li{
        padding-top: 10px;
        }
        .controls{
            padding: 15px;
            margin: 0 15px;
            box-shadow: 1px 0px 5px 5px #eae4e4;
            .dir_btn{
                margin-left: 20%;
            }
            .turn_doc{
                margin-bottom: 30%;
            }
        }
        .modules{
            padding: 15px;
            margin: 15px;
            box-shadow: 1px 0px 5px 5px #eae4e4;
            .row{
                font-weight: 600;
            }
    }
    :deep(.ant-tabs > .ant-tabs-nav) {
        padding-left: 10px;
    }
</style>