import java.util.concurrent.atomic.*;

class TTASLock{
	private AtomicBoolean flag = new AtomicBoolean(false);
	
	void lock(){
		while(true){
			/*
			*ʹ��local spin��������������ν��cache coherence��busռ��
			*/
			while(flag.get()){}//local spin
			if(!flag.getAndSet(true))
				return;
		}
	}
	void unlock(){
		/*
		*��ʱ��ʹ����local spin���߳�cache ʧЧ�������̸߳�дflag��
		*���high contention
		*/
		flag.set(false);
	}
}