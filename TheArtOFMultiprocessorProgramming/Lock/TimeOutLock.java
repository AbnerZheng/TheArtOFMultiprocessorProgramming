import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TimeOutLock{
	static class QNode{
		//�˱�������Ϊ������queue�ģ������𵽱�����ǰ�̶߳�lock��״̬
		public QNode pred = null;
	}
	static QNode AVAILABLE = new QNode();
	AtomicReference<QNode> tail;
	ThreadLocal<QNode> myNode;
	
	public TimeOutLock(){
		tail = new AtomicReference<QNode>(null);
		myNode = new ThreadLocal<QNode>(){
			protected QNode initialValue(){
				return new QNode();
			}
		};
	}
	public boolean tryLock(long time, TimeUnit unit){
		long startTime = System.currentTimeMillis();
		long patience = TimeUnit.MILLISECONDS.convert(time, unit);
		//ȱ�㣺ÿ�ζ�Ҫ���а����new��������������ǰ������reuse node
		QNode qnode = new QNode();
		myNode.set(qnode);
		qnode.pred = null;
		QNode myPred = tail.getAndSet(qnode);
		if(myPred == null || //ǰ�����̳߳�����
			myPred.pred == AVAILABLE){//ǰ���߳���unlock
			return true;
		}
		while(System.currentTimeMillis() - startTime < patience){
			QNode predPred = myPred.pred;//����ǰ���̵߳�lock״̬
			if(predPred == AVAILABLE){//����pred�Ѿ�unlock
				return true;
			}else if(predPred != null){//����pred��ʱ����lock�������Ѿ�redirect����pred��
				myPred = predPred;
			}
		}
		if(!tail.compareAndSet(qnode, myPred))
			qnode.pred = myPred;//redirect���Һ�̲鿴�ҵ�״̬�൱�ڿ��ҵ�pred��״̬��
		return false;
	}
	public void unlock(){
		QNode qnode = myNode.get();
		if(!tail.compareAndSet(qnode, null))//��ʱ��ֹ��һ���߳���queue��
			qnode.pred = AVAILABLE;
	}
}