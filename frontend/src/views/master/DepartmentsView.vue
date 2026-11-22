<template>
  <div>
    <div class="page-header">
      <h2>Departemen</h2>
      <el-button v-if="canEdit" type="primary" @click="openCreate">
        Tambah Departemen
      </el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="code" label="Kode" width="140" />
        <el-table-column prop="name" label="Nama" min-width="200" />
        <el-table-column v-if="canEdit" label="Aksi" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">Edit</el-button>
            <el-button size="small" type="danger" @click="remove(row)">Hapus</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? 'Edit Departemen' : 'Tambah Departemen'"
      width="480px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="Kode" prop="code">
          <el-input v-model="form.code" placeholder="mis. IT" />
        </el-form-item>
        <el-form-item label="Nama" prop="name">
          <el-input v-model="form.name" placeholder="mis. IT Support" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Batal</el-button>
        <el-button type="primary" :loading="saving" @click="save">Simpan</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import {
  createDepartment,
  deleteDepartment,
  getDepartments,
  updateDepartment
} from '../../api/modules/master'

const auth = useAuthStore()
const canEdit = computed(() => auth.user?.role === 'ADMIN')

const rows = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const emptyForm = () => ({ code: '', name: '' })
const form = reactive(emptyForm())

const rules = {
  code: [{ required: true, message: 'Kode wajib diisi', trigger: 'blur' }],
  name: [{ required: true, message: 'Nama wajib diisi', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    rows.value = await getDepartments()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editId.value = null
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { code: row.code, name: row.name })
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (isEdit.value) {
      await updateDepartment(editId.value, { ...form })
      ElMessage.success('Departemen berhasil diperbarui')
    } else {
      await createDepartment({ ...form })
      ElMessage.success('Departemen berhasil dibuat')
    }
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`Hapus departemen "${row.name}"?`, 'Konfirmasi', { type: 'warning' })
  } catch {
    return
  }
  await deleteDepartment(row.id)
  ElMessage.success('Departemen berhasil dihapus')
  await load()
}

onMounted(load)
</script>
