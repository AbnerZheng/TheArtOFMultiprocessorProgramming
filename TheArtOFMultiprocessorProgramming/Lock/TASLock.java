import java.util.concurrent.atomic.*;

public class TASLock{
	private AtomicBoolean flag = new AtomicBoolean(false);
	public void lock(){
		/*��1��flag��ÿһ��getAndSet������д��cache�е�ֵ��
		*����flag�������̹߳������˻�invalidate�����̵߳�cache��
		*�����ν��cache coherence���������ܡ�
		*��2��������ÿ�ζ���дflagʹ��д�Ĺ�����ռ����bus�����bus traffic jam��
		*delay�������̣߳�������delay����Ҫrelease lock���̡߳�
		*/
		while(flag.getAndSet(true)){}
	}
	public void unlock(){
		flag.set(false);
	}
	public boolean isLocked(){
		return flag.get();
	}
}