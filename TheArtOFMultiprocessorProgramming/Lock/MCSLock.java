import java.util.concurrent.atomic.AtomicReference;

public class MCSLock{
	private class QNode{
		public boolean locked = false;
		public QNode next = null;
	}
	AtomicReference<QNode> tail;
	ThreadLocal<QNode> myNode;
	
	public MCSLock(){
		tail = new AtomicReference<QNode>(null);
		myNode = new ThreadLocal<QNode>(){
			protected QNode initialValue(){
				return new QNode();
			}
		};
	}
	
	public void lock(){
		QNode node = myNode.get();
		QNode pred = tail.getAndSet(node);
		if(pred != null){//��ʱqueue����node����ֱ������
			node.locked = true;
			pred.next = node;//���Լ����뵽queue��
			/*
			*ȱ������Ҫǰ�����ı��Լ���locked�򣬿��ܻ����unbounded wait
			*/
			while(node.locked){}//���Լ���locked����spin
		}
	}
	public void unlock(){
		QNode node = myNode.get();
		if(node.next == null){//��ʱ�ҵ�nodeû�к��
			if(tail.compareAndSet(node, null))//�жϴ�ʱqueue���Ƿ�ֻ����һ��node
				return;
			//��ʱtail��ָ���ҵ�node˵�����µ�node���ڼ���queue��
			//��ʱ�ȴ��ҵĺ��node���add���������ҵ�next����Ϊ���node
			while(node.next == null){}
		}
		node.next.locked = false;
		node.next = null;//���ҵ�node��queue��dequeue
	}
}