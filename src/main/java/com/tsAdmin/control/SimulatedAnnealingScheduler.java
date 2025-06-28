package com.tsAdmin.control;

import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.PathNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** 模拟退火调度器 */
public class SimulatedAnnealingScheduler implements Scheduler
{
    Random random = new Random();

    final double MIN_TEMPERATURE = 1.0;
    final double COOLING_RATE = 0.95;
    final int MAX_ITERATION_TIME = 1000;

    /**
     * 将原始Demand列表转换为PathNode节点对 (每个Demand生成两个操作)
     * @param demands 需求列表
     * @return 节点对列表
     */
    private List<PathNode> createDemandPairs(List<Demand> demands)
    {
        List<PathNode> pairs = new ArrayList<>();
        for (Demand demand : demands)
        {
            pairs.add(new PathNode(demand, true));
            pairs.add(new PathNode(demand, false));
        }
        return pairs;
    }

    /**
     * 调度方法，使用模拟退火算法优化分配方案
     * @param cars 车辆列表
     * @param demands 需求列表
     * @return 分配结果列表
     */
    @Override
    public List<Assignment> schedule(List<Car> cars, List<Demand> demands)
    {

        List<PathNode> demandPairs = createDemandPairs(demands);
        List<Assignment> currAssignment = new GreedyScheduler().schedule(cars, demandPairs);

        double currentCost = totalCost(currAssignment);
        double temperature = 1000.0;

        // 迭代模拟退火主循环
        for (int i = 0; i < MAX_ITERATION_TIME && temperature > MIN_TEMPERATURE; i++)
        {
            List<Assignment> newAssignments = generateNeighbor(currAssignment);
            double newCost = totalCost(newAssignments);
            double deltaCost = newCost - currentCost;

            // 判断是否接受新解（更优或以一定概率接受更差解）
            boolean accept = deltaCost < 0 || Math.exp(-deltaCost / temperature) > random.nextDouble();

            if (accept)
            {
                currAssignment = newAssignments;
                currentCost = newCost;
            }
            temperature *= COOLING_RATE;

            // 增加温度扰动，防止陷入局部最优
            if (i % (MAX_ITERATION_TIME / 10) == 0)
            {
                temperature *= 1.05;
            }
        }

        syncAssignmentsToCars(currAssignment);
        return currAssignment;
    }

    // ---------------------- 修改点4: 邻域操作适配PathNode ----------------------
    /**
     * 生成邻域解，对分配方案进行微小变动
     * @param assignments 当前分配方案
     * @return 新的分配方案
     */
    private List<Assignment> generateNeighbor(List<Assignment> assignments) {
        List<Assignment> newAssignments = deepCopyAssignments(assignments);
        Random random = new Random();

        if (random.nextBoolean()) {
            // 交换操作时处理PathNode
            int index1 = random.nextInt(newAssignments.size());
            int index2 = random.nextInt(newAssignments.size());
            if (index1 == index2) return newAssignments;

            Assignment a1 = newAssignments.get(index1);
            Assignment a2 = newAssignments.get(index2);

            if (!a1.getNodeList().isEmpty() && !a2.getNodeList().isEmpty()) {
                // ✅ 修改为操作PathNode
                int dIndex1 = random.nextInt(a1.getNodeList().size());
                int dIndex2 = random.nextInt(a2.getNodeList().size());

                PathNode op1 = a1.getNodeList().get(dIndex1);
                PathNode op2 = a2.getNodeList().get(dIndex2);

                if (canSwap(a1, op1, op2) && canSwap(a2, op2, op1)) {
                    a1.getNodeList().set(dIndex1, op2);
                    a2.getNodeList().set(dIndex2, op1);
                }
            }
        } else {
            // 移动操作时处理PathNode
            int fromIndex = random.nextInt(newAssignments.size());
            int toIndex = random.nextInt(newAssignments.size());
            if (fromIndex == toIndex) return newAssignments;

            Assignment from = newAssignments.get(fromIndex);
            Assignment to = newAssignments.get(toIndex);

            if (!from.getNodeList().isEmpty()) {
                int dIndex = random.nextInt(from.getNodeList().size());
                PathNode op = from.getNodeList().get(dIndex);

                if (canAddDemand(to, op)) {
                    from.getNodeList().remove(dIndex);
                    to.addPathNode(op);
                }
            }
        }

        return newAssignments;
    }

    // ---------------------- 修改点5: 校验逻辑适配PathNode ----------------------
    private boolean canSwap(Assignment assignment, PathNode oldOp, PathNode newOp) {
        // ✅ 计算载重变化时考虑操作类型（装载/卸载）
        double deltaLoad = (newOp.isOrigin() ? newOp.getQuantity() : -newOp.getQuantity())
                - (oldOp.isOrigin() ? oldOp.getQuantity() : -oldOp.getQuantity());
        double newLoad = assignment.getCar().getRemainingLoad() + deltaLoad;
        return newLoad >= 0 && newLoad <= assignment.getCar().getMaxLoad();
    }

    /**
     * 判断是否可以添加操作到分配中
     * @param assignment 分配对象
     * @param op 操作
     * @return 是否可添加
     */
    private boolean canAddDemand(Assignment assignment, PathNode op) {
        // ✅ 根据操作类型计算载重变化
        double delta = op.isOrigin() ? op.getQuantity() : -op.getQuantity();
        double newLoad = assignment.getCar().getRemainingLoad() + delta;
        return newLoad >= 0 && newLoad <= assignment.getCar().getMaxLoad();
    }

    // ---------------------- 修改点6: 深拷贝逻辑调整 ----------------------
    /**
     * 对分配方案进行深拷贝
     * @param assignments 原分配方案
     * @return 拷贝后的分配方案
     */
    private List<Assignment> deepCopyAssignments(List<Assignment> assignments) {
        List<Assignment> copies = new ArrayList<>();
        for (Assignment original : assignments) {
            Assignment copy = new Assignment(new Car(original.getCar()));
            // ✅ 拷贝PathNode而非Demand
            for (PathNode op : original.getNodeList()) {
                copy.addPathNode(new PathNode(op.getDemand(), op.isOrigin()));
            }
            copies.add(copy);
        }
        return copies;
    }

    /**
     * 将Assignment中的操作同步到Car对象的操作列表
     * @param assignments 分配方案列表
     */
    private void syncAssignmentsToCars(List<Assignment> assignments)
    {
        for (Assignment assignment : assignments)
        {
            Car car = assignment.getCar();
            // 清空车辆的操作列表
            car.setNodeList(assignment.getNodeList());
        }
    }

    /**
     * 计算所有分配方案的总成本
     * @param assignments 分配方案列表
     * @return 总成本
     */
    private double totalCost(List<Assignment> assignments) {
        return assignments.stream()
                .mapToDouble(a -> totalCost(a.getCar(), a.getNodeList()))
                .sum();
    }
}
