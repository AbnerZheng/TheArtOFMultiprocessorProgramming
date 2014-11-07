import java.util.concurrent.atomic.*;

public class TTASLock{
	private AtomicBoolean flag = new AtomicBoolean(false);
	
	public void lock(){
		while(true){
			/*
			*ʹ��local spin��������������ν��cache coherence��busռ��
			*/
			while(flag.get()){}//local spin
			if(!flag.getAndSet(true))
				return;
		}
	}
	public void unlock(){
		/*
		*��ʱ��ʹ����local spin���߳�cache ʧЧ�������̸߳�дflag��
		*���high contention
		*/
		flag.set(false);
	}
	public boolean isLocked(){
		return flag.get();
	}
}