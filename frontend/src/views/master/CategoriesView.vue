<template>
  <div>
    <div class="page-header">
      <h2>Kategori</h2>
      <el-button v-if="canEdit" type="primary" @click="openCreate">
        Tambah Kategori
      </el-button>
    </div>

    <div class="toolbar">
      <el-select v-model="filterType" placeholder="Semua tipe" clearable style="width: 200px" @change="load">
        <el-option label="Aset" value="ASSET" />
        <el-option label="Inventori" value="INVENTORY" />
      </el-select>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="code" label="Kode" width="140" />
        <el-table-column prop="name" label="Nama" min-width="200" />
        <el-table-column label="Tipe" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'ASSET' ? 'success' : 'info'">
              {{ row.type === 'ASSET' ? 'Aset' : 'Inventori' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Umur Manfaat (bulan)" width="180">
          <template #default="{ row }">
            {{ row.usefulLifeMonths ?? '—' }}
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
      :title="isEdit ? 'Edit Kategori' : 'Tambah Kategori'"
      width="520px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="Kode" prop="code">
          <el-input v-model="form.code" placeholder="mis. CAT-ASSET" />
        </el-form-item>
        <el-form-item label="Nama" prop="name">
          <el-input v-model="form.name" placeholder="mis. Perangkat Komputer" />
        </el-form-item>
        <el-form-item label="Tipe" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="ASSET">Aset</el-radio>
            <el-radio value="INVENTORY">Inventori</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.type === 'ASSET'" label="Umur Manfaat" prop="usefulLifeMonths">
          <el-input-number v-model="form.usefulLifeMonths" :min="0" :max="1200" placeholder="bulan" />
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
  createCategory,
  deleteCategory,
  getCategories,
  updateCategory
} from '../../api/modules/master'

const auth = useAuthStore()
const canEdit = computed(() => auth.user?.role === 'ADMIN')

const rows = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const filterType = ref('')
const formRef = ref()

const emptyForm = () => ({ code: '', name: '', type: 'ASSET', usefulLifeMonths: null })
const form = reactive(emptyForm())

const rules = {
  code: [{ required: true, message: 'Kode wajib diisi', trigger: 'blur' }],
  name: [{ required: true, message: 'Nama wajib diisi', trigger: 'blur' }],
  type: [{ required: true, message: 'Tipe wajib diisi', trigger: 'change' }]
}

async function load() {
  loading.value = true
  try {
    rows.value = await getCategories(filterType.value ? { type: filterType.value } : undefined)
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
    type: row.type,
    usefulLifeMonths: row.usefulLifeMonths ?? null
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
      type: form.type,
      usefulLifeMonths: form.type === 'ASSET' ? (form.usefulLifeMonths ?? null) : null
    }
    if (isEdit.value) {
      await updateCategory(editId.value, payload)
      ElMessage.success('Kategori berhasil diperbarui')
    } else {
      await createCategory(payload)
      ElMessage.success('Kategori berhasil dibuat')
    }
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`Hapus kategori "${row.name}"?`, 'Konfirmasi', { type: 'warning' })
  } catch {
    return
  }
  await deleteCategory(row.id)
  ElMessage.success('Kategori berhasil dihapus')
  await load()
}

onMounted(load)
</script>
