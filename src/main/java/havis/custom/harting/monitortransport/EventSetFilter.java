package havis.custom.harting.monitortransport;

import havis.transport.Subscription;

public interface EventSetFilter {

	boolean accept(String eventType, Subscription subscription);

}
