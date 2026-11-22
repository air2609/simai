<template>
  <div>
    <h2>Dashboard</h2>
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-label">Total Aset</div>
          <div class="stat-value">—</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-label">Total Nilai Aset</div>
          <div class="stat-value">—</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-label">Item Stok Menipis</div>
          <div class="stat-value">—</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-label">Aset Dalam Perbaikan</div>
          <div class="stat-value">—</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt" shadow="never">
      <p>
        Hubungkan komponen ini ke <code>GET /api/dashboard/summary</code>
        (lihat <code>src/api/modules/dashboard.js</code>).
      </p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.stat-label {
  color: #888;
  font-size: 13px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin-top: 8px;
}
.mt {
  margin-top: 16px;
}
</style>
