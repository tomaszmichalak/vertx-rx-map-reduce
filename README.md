# MapReduce example with RxJava & Vert.x

## Map
When we define a task as a list of small unrelated fragments we can process them independently.
In the code, the task is represented via pl.tomaszmichalak.flowgraph.task.Task.

## Reduce
Task is processed via pl.tomaszmichalak.flowgraph.engine.TaskEngine. It gets fragments from the task
do some calculations based on their flows (pl.tomaszmichalak.flowgraph.fragment.Flow). Please note 
that the flow can be modeled as a graph.

Vert.x applications can be easy scaled with Vert.x Event Bus. In this example we stub Vert.x Event
Bus (Service Proxy) with pl.tomaszmichalak.flowgraph.fragment.FragmentProcessorProxy to simplify 
the code.

## Tests
The algorithm is validated in pl.tomaszmichalak.flowgraph.engine.TaskEngineTest.
 
  