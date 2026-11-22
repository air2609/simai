<template>
  <div>
    <div class="page-header">
      <h2>Vendor</h2>
      <el-button v-if="canEdit" type="primary" @click="openCreate">
        Tambah Vendor
      </el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="code" label="Kode" width="120" />
        <el-table-column prop="name" label="Nama" min-width="180" />
        <el-table-column prop="phone" label="Telepon" width="150" />
        <el-table-column prop="email" label="Email" min-width="180" />
        <el-table-column prop="address" label="Alamat" min-width="200" show-overflow-tooltip />
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
      :title="isEdit ? 'Edit Vendor' : 'Tambah Vendor'"
      width="520px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="Kode" prop="code">
          <el-input v-model="form.code" placeholder="mis. VND-001" />
        </el-form-item>
        <el-form-item label="Nama" prop="name">
          <el-input v-model="form.name" placeholder="mis. PT Sumber Makmur" />
        </el-form-item>
        <el-form-item label="Telepon" prop="phone">
          <el-input v-model="form.phone" placeholder="opsional" />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="opsional" />
        </el-form-item>
        <el-form-item label="Alamat" prop="address">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="opsional" />
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
  createVendor,
  deleteVendor,
  getVendors,
  updateVendor
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

const emptyForm = () => ({ code: '', name: '', phone: '', email: '', address: '' })
const form = reactive(emptyForm())

const rules = {
  code: [{ required: true, message: 'Kode wajib diisi', trigger: 'blur' }],
  name: [{ required: true, message: 'Nama wajib diisi', trigger: 'blur' }],
  email: [{ type: 'email', message: 'Format email tidak valid', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    rows.value = await getVendors()
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
    code: row.code,
    name: row.name,
    phone: row.phone || '',
    email: row.email || '',
    address: row.address || ''
  })
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = {
      code: form.code,
      name: form.name,
      phone: form.phone || null,
      email: form.email || null,
      address: form.address || null
    }
    if (isEdit.value) {
      await updateVendor(editId.value, payload)
      ElMessage.success('Vendor berhasil diperbarui')
    } else {
      await createVendor(payload)
      ElMessage.success('Vendor berhasil dibuat')
    }
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`Hapus vendor "${row.name}"?`, 'Konfirmasi', { type: 'warning' })
  } catch {
    return
  }
  await deleteVendor(row.id)
  ElMessage.success('Vendor berhasil dihapus')
  await load()
}

onMounted(load)
</script>
