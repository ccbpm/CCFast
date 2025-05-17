using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Extensions.Caching.Memory;

namespace BP.Web
{
    public static class CacheHelper
    {
        private static MemoryCache mc = new MemoryCache(new MemoryCacheOptions());

        public static bool Contains(string key)
        {
            return mc.TryGetValue(key, out object result);
        }

        public static T Get<T>(string key)
        {
            if(mc.TryGetValue<T>(key, out T v))
                return v;

            return default(T);
        }

        public static void Add<T>(string key, T v)
        {
            mc.Set<T>(key, v, DateTimeOffset.MaxValue);
        }

        public static void Remove(string key)
        {
            mc.Remove(key);
        }
    }
}
