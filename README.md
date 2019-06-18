## Terminology

`Ship`: Unit of deployment 
  - `Cargo`: Content of the `Ship`, application that is deployed.
  
# Overview

## Use of Zookeeper

Unscheduled ships are added to a Zookeeper Queue under path `/armada/scheduler/queue`.
The *ApiServer* will mostly just write to Zookeeper and use *Etcd* for more 
frequent lookups. The main objective is to keep the amount of *Queries per 
Second* low. After the scheduler assigned a host to a ship, the ship is added
to the hosts queue of unstarted ships, which is under path 
`armada/worker/{worker-id}/queue`. The worker watches it's path and will start
or reschedule jobs that are added to the queue. Zookeeper is also used for
leader election of various components (Scheduler, Controllers, ...). As opposed
to *Etcd*, more components than the *ApiServer* are allowed to access 
*Zookeeper* directly. Everything ther would prevent using it right.

## Use of Etcd
The *ApiServer* is the only component that may read or write to *Etcd*. 
*Etcd* is mainly used to store the cluster state and *ShipBlueprints*.
While the *Advisor* could write a *node*s resource stats in *Zookeeper*, we
have chosen to store them in *etcd* because having every *Advisor* begin 
connected to *Zookeeper* could make it perform worse in large clusters.
The *Advisor* will use the *MonitorService* of the *ApiServer* to update 
resource info. The info and state of a *Ship* is also in etcd. 
