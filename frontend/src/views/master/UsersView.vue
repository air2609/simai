<template>
  <div>
    <div class="page-header">
      <h2>Pengguna</h2>
      <el-button v-if="canEdit" type="primary" @click="openCreate">
        Tambah Pengguna
      </el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="name" label="Nama" min-width="180" />
        <el-table-column prop="email" label="Email" min-width="200" />
        <el-table-column label="Role" width="120">
          <template #default="{ row }">
            <el-tag :type="roleTagType[row.role] || 'info'">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="Departemen" min-width="160">
          <template #default="{ row }">
            {{ row.departmentName || '—' }}
          </template>
        </el-table-column>
        <el-table-column label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.active ? 'success' : 'info'">
              {{ row.active ? 'Aktif' : 'Nonaktif' }}
            </el-tag>
          </template>
        </el-table-column>
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
      :title="isEdit ? 'Edit Pengguna' : 'Tambah Pengguna'"
      width="520px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="Nama" prop="name">
          <el-input v-model="form.name" placeholder="Nama lengkap" />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="email@perusahaan.com" />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            :placeholder="isEdit ? 'Kosongkan jika tidak diubah' : 'Password'"
          />
        </el-form-item>
        <el-form-item label="Role" prop="role">
          <el-select v-model="form.role" placeholder="Pilih role" style="width: 100%">
            <el-option v-for="r in roles" :key="r.value" :label="r.label" :value="r.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="Departemen" prop="departmentId">
          <el-select v-model="form.departmentId" clearable placeholder="Pilih departemen (opsional)" style="width: 100%">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Aktif" prop="active">
          <el-switch v-model="form.active" />
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
  createUser,
  deleteUser,
  getDepartments,
  getUsers,
  updateUser
} from '../../api/modules/master'

const auth = useAuthStore()
const canEdit = computed(() => auth.user?.role === 'ADMIN')

const roles = [
  { value: 'ADMIN', label: 'Admin' },
  { value: 'OPERATOR', label: 'Operator' },
  { value: 'APPROVER', label: 'Approver' },
  { value: 'VIEWER', label: 'Viewer' }
]

const roleTagType = {
  ADMIN: 'danger',
  OPERATOR: 'warning',
  APPROVER: 'primary',
  VIEWER: 'info'
}

function roleLabel(role) {
  return roles.find((r) => r.value === role)?.label || role
}

const rows = ref([])
const departments = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const emptyForm = () => ({
  name: '',
  email: '',
  password: '',
  role: 'VIEWER',
  departmentId: null,
  active: true
})
const form = reactive(emptyForm())

const rules = computed(() => ({
  name: [{ required: true, message: 'Nama wajib diisi', trigger: 'blur' }],
  email: [
    { required: true, message: 'Email wajib diisi', trigger: 'blur' },
    { type: 'email', message: 'Format email tidak valid', trigger: 'blur' }
  ],
  role: [{ required: true, message: 'Role wajib diisi', trigger: 'change' }],
  password: isEdit.value
    ? []
    : [{ required: true, message: 'Password wajib diisi', trigger: 'blur' }]
}))

async function load() {
  loading.value = true
  try {
    const [users, depts] = await Promise.all([getUsers(), getDepartments()])
    rows.value = users
    departments.value = depts
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
  Object.assign(form, {
    name: row.name,
    email: row.email,
    password: '',
    role: row.role,
    departmentId: row.departmentId,
    active: row.active
  })
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = {
      name: form.name,
      email: form.email,
      password: form.password || null,
      role: form.role,
      departmentId: form.departmentId || null,
      active: form.active
    }
    if (isEdit.value) {
      await updateUser(editId.value, payload)
      ElMessage.success('Pengguna berhasil diperbarui')
    } else {
      await createUser(payload)
      ElMessage.success('Pengguna berhasil dibuat')
    }
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`Hapus pengguna "${row.name}"?`, 'Konfirmasi', { type: 'warning' })
  } catch {
    return
  }
  await deleteUser(row.id)
  ElMessage.success('Pengguna berhasil dihapus')
  await load()
}

onMounted(load)
</script>
