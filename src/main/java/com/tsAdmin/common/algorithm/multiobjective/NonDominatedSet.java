package com.tsAdmin.common.algorithm.multiobjective;  // 包声明：多目标优化算法包

import java.util.*;  // 导入Java工具类（List, ArrayList, Collections, Map, HashMap等）

import com.tsAdmin.common.algorithm.multiobjective.MultiObjectiveEvaluator.ObjectiveVector;  // 导入多目标向量类
import com.tsAdmin.model.Assignment;  // 导入分配方案模型类

/**
 * 非支配集管理类
 * 实现非支配解的存储、更新和管理
 * 包括添加新解时的支配关系检查和被支配解的删除逻辑
 */
public class NonDominatedSet
{
    /**
     * 非支配解条目类
     * 同时存储目标向量和对应的解对象
     */
    public static class NonDominatedSolution
    {
        private final ObjectiveVector objectiveVector;  // 目标向量：存储解在多目标空间中的位置
        private final List<Assignment> assignments;     // 对应的分配方案（解）：存储实际的路径分配方案

        /**
         * 构造函数：创建非支配解条目
         * @param objectiveVector 目标向量（解的目标函数值）
         * @param assignments 分配方案列表（解的具体实现）
         */
        public NonDominatedSolution(ObjectiveVector objectiveVector, List<Assignment> assignments)
        {
            this.objectiveVector = objectiveVector;  // 保存目标向量引用
            this.assignments = new ArrayList<>(assignments);  // 创建分配方案列表的副本，防止外部修改
        }

        /**
         * 获取目标向量
         * @return 目标向量（只读）
         */
        public ObjectiveVector getObjectiveVector() { return objectiveVector; }
        
        /**
         * 获取分配方案列表
         * @return 分配方案列表的副本（防止外部修改）
         */
        public List<Assignment> getAssignments() { return new ArrayList<>(assignments); }
    }

    private final List<NonDominatedSolution> solutions;  // 非支配解列表：存储所有非支配解
    private final DominanceComparator comparator;          // 支配关系比较器：用于判断解之间的支配关系

    /**
     * 默认构造函数：创建非支配集，使用默认的支配关系比较器
     */
    public NonDominatedSet()
    {
        this.solutions = new ArrayList<>();  // 初始化非支配解列表为空列表
        this.comparator = new DominanceComparator();  // 创建默认的支配关系比较器
    }

    /**
     * 带参数的构造函数：创建非支配集，使用指定的支配关系比较器
     * @param comparator 支配关系比较器
     */
    public NonDominatedSet(DominanceComparator comparator)
    {
        this.solutions = new ArrayList<>();  // 初始化非支配解列表为空列表
        this.comparator = comparator;  // 使用指定的支配关系比较器
    }

    /**
     * 添加新解到非支配集
     * 根据支配关系决定：
     * 1. 如果新解被支配，则丢弃新解
     * 2. 如果新解支配某些旧解，则删除被支配的旧解，加入新解
     * 3. 如果新解互不支配，则以概率加入新解（这里先直接加入，概率接受在MOSA中实现）
     * 
     * @param newVector 新解的目标向量
     * @param newAssignments 新解的分配方案
     * @return 添加结果
     */
    public AddResult add(ObjectiveVector newVector, List<Assignment> newAssignments)
    {
        // 第一步：检查新解是否被非支配集中的任何解支配
        // 获取非支配集中所有解的目标向量列表
        if (comparator.isDominatedBySet(newVector, getObjectiveVectors()))
        {
            // 如果新解被非支配集中的某个解支配，则新解不是非支配解
            // 丢弃新解，返回失败结果
            return new AddResult(false, Collections.emptyList(), AddResult.Reason.DOMINATED);
        }

        // 第二步：找出被新解支配的旧解
        // 创建列表存储被新解支配的旧解
        List<NonDominatedSolution> dominatedSolutions = new ArrayList<>();
        // 遍历非支配集中的所有解
        for (NonDominatedSolution solution : solutions)  // 顺序检查每个现有非支配解
        {
            // 检查新解是否支配当前旧解
            if (comparator.dominates(newVector, solution.getObjectiveVector()))
            {
                // 如果新解支配旧解，将旧解添加到被支配列表中，稍后统一删除
                dominatedSolutions.add(solution);
            }
        }

        // 第三步：删除被支配的旧解
        // 从非支配集中移除所有被新解支配的旧解
        solutions.removeAll(dominatedSolutions);  // 一次性移除被支配解，避免遍历时修改列表

        // 第四步：添加新解
        // 将新解封装成NonDominatedSolution对象并添加到非支配集
        solutions.add(new NonDominatedSolution(newVector, newAssignments));  // 新解加入帕累托前沿

        // 第五步：判断添加原因
        // 如果被支配的旧解列表为空，说明新解是互补支配的
        // 否则说明新解支配了某些旧解
        AddResult.Reason reason = dominatedSolutions.isEmpty() 
            ? AddResult.Reason.NON_DOMINATED   // 互补支配：新解与所有旧解互不支配
            : AddResult.Reason.DOMINATES_OTHERS;  // 新解支配某些旧解

        // 返回添加结果（成功添加，包含被移除的旧解列表和原因）
        return new AddResult(true, dominatedSolutions, reason);  // 记录新增结果与被移除解
    }

    /**
     * 尝试添加新解（不自动添加，只返回分析结果）
     * 用于在概率接受机制中判断是否应该添加
     * 
     * @param newVector 新解的目标向量
     * @return 添加分析结果
     */
    public AddAnalysis analyzeAdd(ObjectiveVector newVector)
    {
        // 第一步：检查新解是否被非支配集中的任何解支配
        // 获取非支配集中所有解的目标向量，检查新解是否被支配
        boolean isDominated = comparator.isDominatedBySet(newVector, getObjectiveVectors());  // true表示存在更优或表现相同但更优的解

        // 第二步：找出被新解支配的旧解
        // 创建列表存储被新解支配的旧解
        List<NonDominatedSolution> dominatedSolutions = new ArrayList<>();
        // 只有当新解不被支配时，才需要检查新解是否支配旧解
        if (!isDominated)
        {
            // 遍历非支配集中的所有解
        for (NonDominatedSolution solution : solutions)  // 针对每个旧解检查是否被新解支配
            {
                // 检查新解是否支配当前旧解
                if (comparator.dominates(newVector, solution.getObjectiveVector()))
                {
                // 如果新解支配旧解，将旧解添加到被支配列表中，供调用方参考
                    dominatedSolutions.add(solution);
                }
            }
        }

        // 第三步：返回分析结果
        // canAdd = !isDominated：新解不被支配时可以添加
        // isDominated：新解是否被支配
        // dominatedSolutions：被新解支配的旧解列表
        return new AddAnalysis(!isDominated, isDominated, dominatedSolutions);  // canAdd由支配关系决定
    }

    /**
     * 强制添加新解（不检查支配关系，直接添加）
     * 用于特殊情况（如初始化时）
     */
    public void forceAdd(ObjectiveVector newVector, List<Assignment> newAssignments)
    {
        // 直接将新解添加到非支配集，不进行支配关系检查
        // 注意：使用此方法可能导致非支配集中存在被支配的解
        solutions.add(new NonDominatedSolution(newVector, newAssignments));  // 直接插入，不做任何校验
    }

    /**
     * 移除指定的解
     * @param solution 要移除的非支配解
     * @return true表示成功移除，false表示解不存在
     */
    public boolean remove(NonDominatedSolution solution)
    {
        // 从非支配集中移除指定的解
        return solutions.remove(solution);  // remove返回布尔值，可直接复用
    }

    /**
     * 清空非支配集
     * 移除所有非支配解
     */
    public void clear()
    {
        // 清空非支配解列表
        solutions.clear();  // 完整清空内部存储
    }

    /**
     * 获取所有非支配解
     * @return 非支配解列表的副本（防止外部修改）
     */
    public List<NonDominatedSolution> getSolutions()
    {
        // 返回非支配解列表的副本，防止外部直接修改内部列表
        return new ArrayList<>(solutions);  // 防御性复制，避免外部持有引用
    }

    /**
     * 获取所有目标向量
     * @return 所有非支配解的目标向量列表
     */
    public List<ObjectiveVector> getObjectiveVectors()
    {
        // 创建目标向量列表
        List<ObjectiveVector> vectors = new ArrayList<>();
        // 遍历所有非支配解
        for (NonDominatedSolution solution : solutions)
        {
            // 提取每个解的目标向量并添加到列表中
            vectors.add(solution.getObjectiveVector());
        }
        // 返回目标向量列表
        return vectors;  // 返回快照，保证外部不可修改内部状态
    }

    /**
     * 获取非支配集大小
     * @return 非支配集中解的数量
     */
    public int size()
    {
        // 返回非支配解列表的大小
        return solutions.size();  // 直接反映当前帕累托前沿规模
    }

    /**
     * 判断是否为空
     * @return true表示非支配集为空，false表示非支配集不为空
     */
    public boolean isEmpty()
    {
        // 检查非支配解列表是否为空
        return solutions.isEmpty();  // true表示尚未求得任何非支配解
    }

    /**
     * 获取指定索引的解
     * @param index 索引位置（从0开始）
     * @return 指定索引的非支配解
     */
    public NonDominatedSolution get(int index)
    {
        // 返回指定索引位置的非支配解
        return solutions.get(index);  // 由调用方保证索引合法
    }

    /**
     * 添加结果类
     */
    public static class AddResult
    {
        public enum Reason
        {
            DOMINATED,         // 新解被支配
            DOMINATES_OTHERS,  // 新解支配某些旧解
            NON_DOMINATED      // 新解互补支配
        }

        private final boolean added;                    // 是否成功添加
        private final List<NonDominatedSolution> removed; // 被移除的旧解列表
        private final Reason reason;                     // 添加原因

        public AddResult(boolean added, List<NonDominatedSolution> removed, Reason reason)
        {
            this.added = added;
            this.removed = new ArrayList<>(removed);
            this.reason = reason;
        }

        public boolean isAdded() { return added; }
        public List<NonDominatedSolution> getRemoved() { return new ArrayList<>(removed); }
        public Reason getReason() { return reason; }

        @Override
        public String toString()
        {
            return String.format("添加结果: %s, 原因: %s, 移除%d个旧解", 
                added ? "成功" : "失败", reason, removed.size());
        }
    }

    /**
     * 添加分析结果类（用于概率接受）
     */
    public static class AddAnalysis
    {
        private final boolean canAdd;                    // 是否可以添加
        private final boolean isDominated;              // 是否被支配
        private final List<NonDominatedSolution> dominated; // 被新解支配的旧解列表

        public AddAnalysis(boolean canAdd, boolean isDominated, List<NonDominatedSolution> dominated)
        {
            this.canAdd = canAdd;
            this.isDominated = isDominated;
            this.dominated = new ArrayList<>(dominated);
        }

        public boolean canAdd() { return canAdd; }
        public boolean isDominated() { return isDominated; }
        public List<NonDominatedSolution> getDominated() { return new ArrayList<>(dominated); }
    }


    /**
     * 根据目标类型获取最优值
     * 
     * @param objectiveType 目标类型（DISTANCE/TIME/COST）
     * @return 该目标的最优值（最小值，因为所有目标都是最小化目标）
     */
    public double getBestValue(MultiObjectiveEvaluator.ObjectiveType objectiveType)
    {
        // 如果非支配集为空，返回 NaN 表示不存在最优值
        if (solutions.isEmpty())
        {
            return Double.NaN;
        }

        // 初始化最优值为 +∞
        double best = Double.POSITIVE_INFINITY;
        // 遍历所有非支配解
        for (NonDominatedSolution solution : solutions)
        {
            // 获取当前解在指定目标类型上的值
            double value = solution.getObjectiveVector().getComparableValue(objectiveType);  // 所有目标已统一为“越小越好”
            // 如果当前值更小，更新最优值
            if (value < best)
            {
                best = value;
            }
        }
        // 如果未找到有效值，返回 NaN
        return best == Double.POSITIVE_INFINITY ? Double.NaN : best;  // 为空时返回NaN标示无值
    }

    /**
     * 根据目标类型获取最差值
     * 
     * @param objectiveType 目标类型（DISTANCE/TIME/COST）
     * @return 该目标的最差值（最大值）
     */
    public double getWorstValue(MultiObjectiveEvaluator.ObjectiveType objectiveType)
    {
        // 如果非支配集为空，返回 NaN 表示不存在最差值
        if (solutions.isEmpty())
        {
            return Double.NaN;
        }

        // 初始化最差值为 -∞
        double worst = Double.NEGATIVE_INFINITY;
        // 遍历所有非支配解
        for (NonDominatedSolution solution : solutions)
        {
            // 获取当前解在指定目标类型上的值
            double value = solution.getObjectiveVector().getComparableValue(objectiveType);  // 仍使用统一比较值
            // 如果当前值更大，更新最差值
            if (value > worst)
            {
                worst = value;
            }
        }
        // 如果未找到有效值，返回 NaN
        return worst == Double.NEGATIVE_INFINITY ? Double.NaN : worst;  // 为空时返回NaN
    }

    /**
     * 获取所有目标的最优值范围
     * 
     * @return 目标类型到值范围的映射，每个范围是[min, max]数组
     */
    public Map<MultiObjectiveEvaluator.ObjectiveType, double[]> getValueRange()
    {
        // 创建映射表，存储每个目标类型的值范围（在“越小越好”的统一坐标系下）
        Map<MultiObjectiveEvaluator.ObjectiveType, double[]> range = new HashMap<>();
        
        // 遍历所有目标类型（DISTANCE, TIME, COST）
        for (MultiObjectiveEvaluator.ObjectiveType type : MultiObjectiveEvaluator.ObjectiveType.values())
        {
            double min = getBestValue(type);  // 已是minimization范式下的全局最优
            double max = getWorstValue(type);  // 同一目标维度的最差解，用于范围归一化

            if (Double.isNaN(min) || Double.isNaN(max))  // 没有有效解时，用NaN告知调用方
            {
                range.put(type, new double[]{Double.NaN, Double.NaN});
                continue;
            }

            range.put(type, new double[]{min, max});  // 存储范围[min,max]供绘图或归一化使用
        }
        
        // 返回所有目标类型的值范围映射
        return range;
    }
}

