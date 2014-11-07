import java.util.concurrent.atomic.*;

public class BackoffLock{
	private AtomicBoolean state = new AtomicBoolean(false);
	private final int MIN_DELAY;//you choose a appropriate value
	private final int MAX_DELAY;//you choose a appropriate value
	/*
	*�������������MIN_DELAY��MAX_DELAY��ֵ��ѡȡ��ֱ�ӵĹ�ϵ��
	*�����е��̶߳���ͬһ��location��spin��ʹ�õ��ɹ�lockʱ���cache-coherence traffic
	*/
	public BackoffLock(int min, int max){
		MIN_DELAY = min;
		MAX_DELAY = max;
	}
	public void lock(){
		Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
		while(true){
			while(state.get()){}
			if(!state.getAndSet(true)){
				return;
			}else{
				try {
					backoff.backoff();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	public void unlock(){
		state.set(false);
	}
}
