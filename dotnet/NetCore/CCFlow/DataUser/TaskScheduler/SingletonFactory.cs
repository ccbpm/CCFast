using Quartz.Spi;
using Quartz;
using System;

namespace CCFlow.NetCore.DataUser.TaskScheduler
{
    public class SingletonFactory: IJobFactory
    {
        private readonly IServiceProvider _serviceProvider;

        public SingletonFactory(IServiceProvider serviceProvider)
        {
            _serviceProvider = serviceProvider;
        }

        public IJob NewJob(TriggerFiredBundle bundle, IScheduler scheduler) {
            return _serviceProvider.GetService(bundle.JobDetail.JobType) as IJob;
        }

        public void ReturnJob(IJob job)
        {
        }
    }
}
