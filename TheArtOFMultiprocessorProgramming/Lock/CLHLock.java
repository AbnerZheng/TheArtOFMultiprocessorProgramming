import java.util.concurrent.atomic.AtomicReference;

class QNode{
	public boolean locked = false;
}

public class CLHLock{
	private AtomicReference<QNode> tail;
	private ThreadLocal<QNode> myPred;
	private ThreadLocal<QNode> myNode;
	
	public CLHLock(){
		//ԭ������tail��ʼ����ʱ��Ӧ��ָ��һ��QNode�����һ�������thread�Ͳ���������ǰ��QNode��
		//tail = new AtomicReference<QNode>(null);
		tail = new AtomicReference<QNode>(new QNode());
		myNode = new ThreadLocal<QNode>(){
			protected QNode initialValue(){
				return new QNode();
			}
		};
		myPred = new ThreadLocal<QNode>(){
			protected QNode initialValue(){
				return null;
			}
		};
	}
	
	public void lock(){
		QNode node = myNode.get();
		node.locked = true;
		QNode pred = tail.getAndSet(node);
		myPred.set(pred);
		/*
		*�����Ƶ�wait����ȱ�ݣ���ʵ��pred node��locked����spin��
		*��cache-less NUMA�ܹ��Ļ��������ܻ���poor
		*/
		while(pred.locked){}
	}
	public void unlock(){
		QNode node = myNode.get();
		node.locked = false;
		//����pred node
		myNode.set(myPred.get());
	}
}