package com.tsAdmin.control.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.tsAdmin.common.PathNode;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.CarList;

/** 
 * 模拟退火调度器
 * 
 * 算法原理：
 * 模拟退火算法是一种启发式优化算法，模拟金属冷却过程。
 * 在高温时，算法有较大概率接受劣解，避免陷入局部最优；
 * 随着温度降低，算法逐渐倾向于只接受更优的解。
 * 
 * 核心特点：
 * 1. 成对操作：同一订单的起点-终点必须成对交换或转移
 * 2. 约束检查：确保载重和体积约束始终满足
 * 3. 温度控制：通过温度参数控制接受劣解的概率
 * 4. 扰动机制：定期增加温度，防止过早收敛到局部最优
 */
public class SimulatedAnnealingScheduler extends Scheduler
{
    // 使用类级别的Random对象，避免重复创建，提高性能
    // 同时确保随机性的一致性
    Random random = new Random();

    // ========== 模拟退火算法参数 ==========
    final double MIN_TEMPERATURE = 1.0;        // 最小温度，低于此温度停止迭代
    final double COOLING_RATE = 0.95;          // 冷却率，控制温度下降速度
    final int MAX_ITERATION_TIME = 500;       // 最大迭代次数，防止无限循环


    /**
     * 调度方法，使用模拟退火算法优化分配方案
     * 
     * 算法流程：
     * 1. 使用贪心算法生成初始解
     * 2. 通过模拟退火迭代优化解
     * 3. 将最终结果同步回原始车辆
     * 
     * 核心改进：
     * - 约束检查采用模拟整个序列执行过程，确保准确性
     * - 成对操作保证同一订单的起点终点不分离
     * - 深拷贝避免修改原始数据
     * 
     * @param cars 车辆列表
     * @param demands 需求列表
     * @return 分配结果列表
     */
    @Override
    public List<Assignment> schedule()
    {
        // 使用贪心算法生成初始解，作为模拟退火的起点
        List<Assignment> currAssignment = new GreedyScheduler().schedule();

        // 计算初始解的最大单车cost
        double currentCost = maxCarCost(currAssignment);
        
        // 设置初始温度，控制接受劣解的概率
        double temperature = 1000.0;

        // ========== 模拟退火主循环 ==========
        for (int i = 0; i < MAX_ITERATION_TIME && temperature > MIN_TEMPERATURE; i++)
        {
            // TODO
            // 生成邻域解（通过交换或转移操作）
            List<Assignment> newAssignments = generateNeighbor(currAssignment);
            
            // 计算新解的最大单车cost
            double newCost = maxCarCost(newAssignments);
            
            // 计算成本差
            double deltaCost = newCost - currentCost;

            // ========== 判断是否接受新解 ==========
            // 接受条件：
            // 1. 新解更优（deltaCost < 0）
            // 2. 或者以一定概率接受劣解（模拟退火的核心思想）
            boolean accept = deltaCost < 0 || Math.exp(-deltaCost / temperature) > random.nextDouble();

            if (accept)
            {
                // 接受新解，更新当前解和成本
                currAssignment = newAssignments;
                currentCost = newCost;
            }
            // 降温：随着迭代进行，接受劣解的概率逐渐降低
            temperature *= COOLING_RATE;

            // ========== 温度扰动机制 ==========
            // 每隔一定迭代次数增加温度，防止陷入局部最优
            if (i % (MAX_ITERATION_TIME / 10) == 0)
            {
                temperature *= 1.05; // 增加5%的温度
            }
        }

        // ========== 同步结果 ==========
        // 将优化后的分配方案同步回原始车辆对象
        syncAssignmentsToCars(currAssignment, CarList.carList.values());
        
        return currAssignment;
    }

    /**
     * 生成邻域解，对分配方案进行微小变动
     * 
     * 核心逻辑：同一订单的起点终点成对交换或转移
     * 
     * 操作类型：
     * 1. 交换操作：两个车辆序列中的订单对相互交换
     * 2. 转移操作：将一个订单对从一个车辆转移到另一个车辆
     * 
     * 约束检查：
     * - 使用模拟整个序列执行过程进行约束检查
     * - 确保载重和体积约束始终满足
     * - 保证同一订单的起点终点不分离
     * 
     * @param assignments 当前分配方案
     * @return 新的分配方案
     */
private List<Assignment> generateNeighbor(List<Assignment> assignments) {
        // 深拷贝当前分配方案，避免修改原始数据
        List<Assignment> newAssignments = deepCopyAssignments(assignments);
        
        // 随机选择交换方式
        int swapType = random.nextInt(3); // 0, 1, 2，分别代表三种交换方式

        // ==================== 交换操作：同一订单的起点终点成对交换 ====================
        if (swapType == 0) {
            // 交换操作：同一订单的起点终点成对交换
            int index1 = random.nextInt(newAssignments.size());
            int index2 = random.nextInt(newAssignments.size());
            if (index1 == index2) return newAssignments; // 如果选择了同一个序列，直接返回

            Assignment a1 = newAssignments.get(index1); // 第一个车辆序列
            Assignment a2 = newAssignments.get(index2); // 第二个车辆序列

            // 确保两个序列都不为空
            if (!a1.getNodeList().isEmpty() && !a2.getNodeList().isEmpty()) {

                // 在第一个序列中查找订单对
                int dIndex1 = random.nextInt(a1.getNodeList().size());
                PathNode op1 = a1.getNodeList().get(dIndex1); // 获取选中的操作
                String orderUUID1 = op1.getDemand().getUUID(); // 获取订单UUID

                PathNode pairOp1 = findPairNode(a1.getNodeList(), orderUUID1, op1.isOrigin());
                if (pairOp1 == null) return newAssignments;

                // 在第二个序列中查找订单对
                int dIndex2 = random.nextInt(a2.getNodeList().size());
                PathNode op2 = a2.getNodeList().get(dIndex2); // 获取选中的操作
                String orderUUID2 = op2.getDemand().getUUID(); // 获取订单UUID

                PathNode pairOp2 = findPairNode(a2.getNodeList(), orderUUID2, op2.isOrigin());
                if (pairOp2 == null) return newAssignments;

                // 确定起点和终点节点
                PathNode start1, end1, start2, end2;
                if (op1.isOrigin()) {
                    start1 = op1;
                    end1 = pairOp1;
                } else {
                    start1 = pairOp1;
                    end1 = op1;
                }
                
                if (op2.isOrigin()) {
                    start2 = op2;
                    end2 = pairOp2;
                } else {
                    start2 = pairOp2;
                    end2 = op2;
                }
                  // 执行交换操作
                if (canSwap(a1, start1, end1, start2, end2) && canSwap(a2, start2, end2, start1, end1)) {
                    int startIndex1 = a1.getNodeList().indexOf(start1);
                    int endIndex1 = a1.getNodeList().indexOf(end1);
                    int startIndex2 = a2.getNodeList().indexOf(start2);
                    int endIndex2 = a2.getNodeList().indexOf(end2);

                    // 交换起点
                    a1.getNodeList().set(startIndex1, start2);
                    a2.getNodeList().set(startIndex2, start1);

                    // 交换终点
                    a1.getNodeList().set(endIndex1, end2);
                    a2.getNodeList().set(endIndex2, end1);
                }
            }
        }

        // ==================== 移动操作：同一订单的起点终点成对转移 ====================
        else if (swapType == 1) {
            // 移动操作：同一订单的起点终点成对转移
            int fromIndex = random.nextInt(newAssignments.size());
            int toIndex = random.nextInt(newAssignments.size());
            if (fromIndex == toIndex) return newAssignments; // 如果选择了同一个序列，直接返回

            Assignment from = newAssignments.get(fromIndex); // 源车辆序列
            Assignment to = newAssignments.get(toIndex);     // 目标车辆序列

            // 确保源序列不为空
            if (!from.getNodeList().isEmpty()) {

                int dIndex = random.nextInt(from.getNodeList().size());
                PathNode op = from.getNodeList().get(dIndex);
                String orderUUID = op.getDemand().getUUID();

                PathNode pairOp = findPairNode(from.getNodeList(), orderUUID, op.isOrigin());
                if (pairOp == null) return newAssignments;

                if (canAddDemand(to, op, pairOp)) {
                    PathNode startNode, endNode;
                    if (op.isOrigin()) {
                        startNode = op;
                        endNode = pairOp;
                    } else {
                        startNode = pairOp;
                        endNode = op;
                    }

                    // 从源序列中移除
                    List<PathNode> nodesToRemove = new ArrayList<>();
                    nodesToRemove.add(op);
                    nodesToRemove.add(pairOp);
                    from.getNodeList().removeAll(nodesToRemove);

                    // 添加到目标序列末尾
                    to.addPathNode(startNode);
                    to.addPathNode(endNode);
                }
            }
        }

        // ==================== 处理只有终点节点的情况 ====================
        else if (swapType == 2) {
            // 处理只有终点节点的情况，随机移动终点节点
            for (Assignment assignment : newAssignments) {
                List<PathNode> nodeList = assignment.getNodeList();
                List<PathNode> endNodes = new ArrayList<>();

                // 收集所有只有终点的节点
                for (PathNode node : nodeList) {
                    if (!node.isOrigin()) {
                        endNodes.add(node); // 找到终点，存储
                    }
                }

                // 如果有终点节点，随机移动到序列中其他位置
                if (!endNodes.isEmpty()) {
                    PathNode randomEndNode = endNodes.get(random.nextInt(endNodes.size())); // 随机选取一个终点
                    nodeList.remove(randomEndNode);  // 从原位置移除该终点
                    int newIndex = random.nextInt(nodeList.size() + 1);  // 随机确定新位置
                    nodeList.add(newIndex, randomEndNode);  // 将终点添加到新位置
                }
            }
        }

        return newAssignments;
    }
    /**
     * 在节点列表中查找同一订单的配对节点
     * 
     * 功能说明：
     * 每个订单都有两个操作：起点（装货）和终点（卸货）
     * 如果你选中了"装货"操作，这个方法会帮你找到"卸货"操作
     * 如果你选中了"卸货"操作，这个方法会帮你找到"装货"操作
     * 
     * 查找逻辑：
     * 1. 遍历所有节点，找到UUID相同的订单
     * 2. 检查操作类型是否相反（起点找终点，终点找起点）
     * 3. 返回第一个满足条件的配对节点
     * 
     * 重要说明：
     * - 如果找不到配对节点，说明该订单的起点已经被前端获取并删除
     * - 这种情况下应该跳过该操作，避免起点终点分离
     * 
     * @param nodeList 要搜索的节点列表
     * @param orderUUID 要查找的订单UUID（比如"orderA"）
     * @param isOrigin 当前选中的节点是什么类型
     *                true = 当前选中的是起点（装货）
     *                false = 当前选中的是终点（卸货）
     * @return 配对的节点，如果找不到返回null
     */
    private PathNode findPairNode(List<PathNode> nodeList, String orderUUID, boolean isOrigin)
    {
        // 遍历所有节点，找到配对的节点
        for (PathNode node : nodeList)
        {
            // 检查两个条件：
            // 条件1：必须是同一个订单（UUID相同）
            boolean sameOrder = node.getDemand().getUUID().equals(orderUUID);
            
            // 条件2：必须是相反的操作类型
            // 如果当前选中的是起点，就要找终点
            // 如果当前选中的是终点，就要找起点
            boolean oppositeType = node.isOrigin() != isOrigin;
            
            // 两个条件都满足，就找到了配对节点
            if (sameOrder && oppositeType) {
                return node;
            }
        }
        
        // 如果遍历完所有节点都没找到，返回null
        return null;
    }

    /**
     * 检查两对起点-终点交换后的约束是否满足
     * 
     * 修正后的逻辑：
     * 1. 先执行交换操作：将oldStart替换为newStart，将oldEnd替换为newEnd
     * 2. 然后模拟整个序列执行过程：从头到尾遍历序列，执行交换后的操作
     * 3. 在每个步骤检查约束：确保载重和体积约束始终满足
     * 
     * 重要说明：
     * - 不是跳过要交换的操作，而是用新操作替换旧操作
     * - 必须遍历到序列的最后，确保整个执行过程都满足约束
     * - 这样可以准确模拟交换后的实际执行情况
     * 
     * @param assignment 要检查的车辆分配方案
     * @param oldStart 原序列中的起点操作
     * @param oldEnd 原序列中的终点操作
     * @param newStart 新序列中的起点操作
     * @param newEnd 新序列中的终点操作
     * @return true表示可以交换，false表示不能交换
     */
    private boolean canSwap(Assignment assignment, PathNode oldStart, PathNode oldEnd, PathNode newStart, PathNode newEnd) {
        // 创建车辆状态的临时副本用于模拟
        double remainingLoad = assignment.getCar().getRemainingLoad();
        double remainingVolume = assignment.getCar().getRemainingVolume();
        
        // ========== 模拟执行整个序列（执行交换后的序列） ==========
        for (PathNode existingNode : assignment.getNodeList()) {
            // 检查当前节点是否需要交换
            PathNode nodeToExecute;
            if (existingNode == oldStart) {
                // 这个位置原来是oldStart，现在应该是newStart
                nodeToExecute = newStart;
            } else if (existingNode == oldEnd) {
                // 这个位置原来是oldEnd，现在应该是newEnd
                nodeToExecute = newEnd;
            } else {
                // 这个位置没有变化，执行原来的节点
                nodeToExecute = existingNode;
            }
            
            // 模拟执行当前操作
            if (nodeToExecute.isOrigin())
            {
                // 装货：减少剩余载重和体积
                remainingLoad -= nodeToExecute.getDemand().getQuantity();
                remainingVolume -= nodeToExecute.getDemand().getVolume();
            }
            else
            {
                // 卸货：增加剩余载重和体积
                remainingLoad += nodeToExecute.getDemand().getQuantity();
                remainingVolume += nodeToExecute.getDemand().getVolume();
            }
            
            // 检查约束是否满足
            if (remainingLoad < 0 || remainingLoad > assignment.getCar().getMaxLoad() || 
                remainingVolume < 0 || remainingVolume > assignment.getCar().getMaxVolume())
            {
                return false; // 约束不满足，不能交换
            }
        }
        
        return true; // 整个序列执行完毕，所有约束都满足
    }

    /**
     * 判断是否可以添加一对起点-终点操作到分配中
     * 重要：需要模拟整个序列执行过程，确保添加后所有约束都满足
     * 
     * @param assignment 要检查的车辆分配方案
     * @param startNode 起点操作（装货操作）
     * @param endNode 终点操作（卸货操作）
     * @return true表示可以添加，false表示不能添加
     */
    private boolean canAddDemand(Assignment assignment, PathNode startNode, PathNode endNode) {
        // 创建车辆状态的临时副本用于模拟
        double remainingLoad = assignment.getCar().getRemainingLoad();
        double remainingVolume = assignment.getCar().getRemainingVolume();
        
        // ========== 第一步：模拟执行现有序列 ==========
        for (PathNode existingNode : assignment.getNodeList()) {
            // 模拟执行现有操作
            if (existingNode.isOrigin()) {
                // 装货：减少剩余载重和体积
                remainingLoad -= existingNode.getDemand().getQuantity();
                remainingVolume -= existingNode.getDemand().getVolume();
            } else {
                // 卸货：增加剩余载重和体积
                remainingLoad += existingNode.getDemand().getQuantity();
                remainingVolume += existingNode.getDemand().getVolume();
            }
            
            // 检查约束是否满足
            if (remainingLoad < 0 || remainingLoad > assignment.getCar().getMaxLoad() || 
                remainingVolume < 0 || remainingVolume > assignment.getCar().getMaxVolume()) {
                return false; // 现有序列已经违反约束
            }
        }
        
        // ========== 第二步：模拟执行新添加的起点-终点对 ==========
        // 模拟起点操作（装货）
        if (startNode.isOrigin()) {
            remainingLoad -= startNode.getDemand().getQuantity();
            remainingVolume -= startNode.getDemand().getVolume();
        } else {
            remainingLoad += startNode.getDemand().getQuantity();
            remainingVolume += startNode.getDemand().getVolume();
        }
        
        // 检查起点操作后的约束
        if (remainingLoad < 0 || remainingLoad > assignment.getCar().getMaxLoad() || 
            remainingVolume < 0 || remainingVolume > assignment.getCar().getMaxVolume()) {
            return false;
        }
        
        // 模拟终点操作（卸货）
        if (endNode.isOrigin()) {
            remainingLoad -= endNode.getDemand().getQuantity();
            remainingVolume -= endNode.getDemand().getVolume();
        } else {
            remainingLoad += endNode.getDemand().getQuantity();
            remainingVolume += endNode.getDemand().getVolume();
        }
        
        // 检查终点操作后的约束
        if (remainingLoad < 0 || remainingLoad > assignment.getCar().getMaxLoad() ||
            remainingVolume < 0 || remainingVolume > assignment.getCar().getMaxVolume())
            {
            return false;
        }
        
        return true; // 所有约束都满足
    }

    /**
     * 对分配方案进行深拷贝
     * 功能：创建分配方案的完全独立副本，避免修改原始数据
     * 重要性：模拟退火算法需要多次尝试不同的邻域解，如果不深拷贝会相互影响
     * 
     * @param assignments 原分配方案
     * @return 拷贝后的分配方案
     */
    private List<Assignment> deepCopyAssignments(List<Assignment> assignments)
    {
        List<Assignment> copies = new ArrayList<>();
        
        // 遍历每个分配方案，创建深拷贝
        for (Assignment original : assignments) {
            // 创建新的Assignment对象，包含车辆的深拷贝
            Assignment copy = new Assignment(new Car(original.getCar()));
            
            // 拷贝PathNode列表，确保每个PathNode都是独立的
            for (PathNode op : original.getNodeList())
            {
                // 创建新的PathNode，保持相同的Demand引用和isOrigin状态
                copy.addPathNode(new PathNode(op.getDemand(), op.isOrigin()));
            }
            copies.add(copy);
        }
        return copies;
    }

    /**
     * 将Assignment中的操作同步到原始Car对象的操作列表
     * 功能：将优化后的分配方案同步回原始车辆对象，确保前端能获取到最新的调度结果
     * 必要性：模拟退火算法在深拷贝的Assignment上工作，需要将最终结果同步回原始车辆
     * 
     * @param assignments 优化后的分配方案列表
     * @param originalCars 原始车辆列表（前端实际使用的车辆对象）
     */
    public void syncAssignmentsToCars(List<Assignment> assignments, Collection<Car> originalCars)
    {
        // 遍历每个优化后的分配方案
        for (Assignment assignment : assignments)
        {
            Car car = assignment.getCar(); // 获取分配方案中的车辆
            
            // 在原始车辆列表中查找对应的车辆
            for (Car originalCar : originalCars) {
                if (car.getUUID().equals(originalCar.getUUID()))
                {
                    // 找到匹配的车辆，开始同步操作
                    
                    // 清空原始车辆的操作列表，准备接收新的操作序列
                    originalCar.getNodeList().clear();
                    
                    // 将Assignment中的操作添加到Car的pathNodes中
                    // 这样前端就能获取到优化后的操作序列
                    for (PathNode node : assignment.getNodeList())
                    {
                        originalCar.addPathNode(node);
                    }
                }
            }
        }
    }

    // 最大单车cost目标函数
    private double maxCarCost(List<Assignment> assignments) {
        double max = 0;
        for (Assignment a : assignments) {
            double cost = totalCost(a.getCar(), a.getNodeList());
            if (cost > max) max = cost;
        }
        return max;
    }
}
