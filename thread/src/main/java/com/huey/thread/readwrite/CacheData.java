package com.huey.thread.readwrite;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: huey
 * @Desc:  在降级成功后，也就是持有写锁的时候同时申请并且获取了读锁后，此时直接释放写锁，但是不释放读锁，这样就可以提高利用
 * 效率，下面的代码更新缓存的时候，如何利用锁的降级功能
 */

/**
 * 在这段代码中有一个读写锁，最重要的就是中间的 processcacheddata 方法，在这个方法中，会首先获取到读锁，
 * 也就是 rwl.readlock().lock()，它去判断当前的缓存是否有效，如果有效那么就直接跳过整个 if 语句，
 * 如果已经失效，代表我们需要更新这个缓存了。由于我们需要更新缓存，所以之前获取到的读锁是不够用的，
 * 我们需要获取写锁。 在获取写锁之前，我们首先释放读锁，然后利用 rwl.writelock().lock() 来获取到写锁，
 * 然后是经典的 try finally 语句，在 try 语句中我们首先判断缓存是否有效，因为在刚才释放读锁和获取写锁的过程中，
 * 可能有其他线程抢先修改了数据，所以在此我们需要进行二次判断。 如果我们发现缓存是无效的，就用 new object()
 * 这样的方式来示意，获取到了新的数据内容，并把缓存的标记位设置为 ture，让缓存变得有效。
 * 由于我们后续希望打印出 data 的值，所以不能在此处释放掉所有的锁。我们的选择是在不释放写锁的情况下直接获取读锁，
 * 也就是 rwl.readlock().lock() 这行语句所做的事情，然后，在持有读锁的情况下释放写锁，最后，
 * 在最下面的 try 中把 data 的值打印出来。 这就是一个非常典型的利用锁的降级功能的代码。
 * 你可能会想，我为什么要这么麻烦进行降级呢？我一直持有最高等级的写锁不就可以了吗？这样谁都没办法来影响到我自己的工作，
 * 永远是线程安全的。 为什么需要锁的降级？ 如果我们在刚才的方法中，一直使用写锁，最后才释放写锁的话，
 * 虽然确实是线程安全的，但是也是没有必要的，因为我们只有一处修改数据的代码： data = new object();
 * 后面我们对于 data 仅仅是读取。如果还一直使用写锁的话，就不能让多个线程同时来读取了，持有写锁是浪费资源的，
 * 降低了整体的效率，所以这个时候利用锁的降级是很好的办法，可以提高整体性能
 */
public class CacheData {

    Object data;
    volatile boolean cacheValid;
    final ReentrantReadWriteLock rwl=new ReentrantReadWriteLock();
    void processCacheData(){
        //最开始读取 获取读锁
        rwl.readLock().lock();
        if(!cacheValid){
            //发现缓存失效，那么就需要写入，所以现在需要获取写锁，由于所不支持升级，
            // 所以需要写锁之前，必须首先降级读锁。
            rwl.readLock().unlock();
            //获取写锁
            rwl.writeLock().lock();
            try {
                //这里需要再次判断数据的有效性，因为我们在释放读锁和写锁的时，这个间隙，可能有其它的线程会修改了数据
                if(!cacheValid){
                    data=new Object();
                    cacheValid=true;
                }
                //rw.readLock().lock(); 是否存在执行不到的情况
                //在不释放写锁的情况下，直接获取读锁，就是降级
                // 思考 ：当写锁拿到，读锁是丢在等待队列中进行等待，是不能获取读锁的，那么这里直接获取是获取不到的么？
                rwl.readLock().lock();
                // 是不是在尝试获取锁，然后等待写锁释放就直接拿取？
                // 获取读锁的原因： 不放弃写锁就获取读锁，代表业务逻辑不中断，否则还要重新竞争锁
                //多个线程不能同时拿读锁和写锁，对于同一个线程而言，是可以的哈
            }finally {
                //释放了写锁，但是依然持有读锁，这样的话，多个线程同时读取，提高效率。
                rwl.writeLock().lock();
            }

            try {
                System.out.println(data);
            }finally {
                rwl.readLock().unlock();
            }

        }

    }

}
